package util

import java.io.{File, IOException}
import java.nio.file.StandardWatchEventKinds._
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import akka.actor.ActorSystem
import akka.pattern.ask
import com.typesafe.jse.Engine.JsExecutionResult

import com.typesafe.jse.{Trireme, Engine}
import akka.util.Timeout
import play.api.libs.json.Json
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable
import util.FuncTransform._

class GameJsxCompiler {
  private val watcher = FileSystems.getDefault.newWatchService()
  private val buildFile = new File("conf/build.js")


  def shutdown() = {
    watcher.close()
  }

  private def compileJSX(file: String): Unit = {
    implicit val system = ActorSystem("jse-system")
    implicit val timeout = Timeout(5.seconds)

    val files = immutable.Seq(Json.toJson(Seq(file)).toString())
    val future = system.actorOf(Trireme.props(), "engine").ask(Engine.ExecuteJs(buildFile, files, timeout.duration))
    future.onComplete(result => {
      val jsResult = result.get.asInstanceOf[JsExecutionResult]
      if(jsResult.exitValue != 0) {
        new String(jsResult.error.toArray, "UTF-8").split("\n").foreach(System.err.println(_: String))
      }
    })
    Await.result(future, timeout.duration)
    system.shutdown()
  }

  private def checkCompile(path: Path): Unit = {
    val pathString = path.toAbsolutePath.toString
    if(pathString.endsWith(".jsx")) {
      println(s"compiling '$pathString'" )
      compileJSX(pathString)
    }
  }

  def apply(): Unit = {

    Files.walkFileTree(new File("games").toPath, new SimpleFileVisitor[Path] {
      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        checkCompile(file)
        FileVisitResult.CONTINUE
      }
      override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
        dir.toAbsolutePath.register(watcher, ENTRY_MODIFY)
        FileVisitResult.CONTINUE
      }
    })


    try {
      while(true) {
        val key = watcher.take()
        key.pollEvents().forEach( (event:WatchEvent[_]) => {
          checkCompile(key.watchable().asInstanceOf[Path].resolve(event.context().asInstanceOf[Path]))
        })
        key.reset()
      }
    } catch {
      case x @ (_:IOException | _: ClosedWatchServiceException) =>
        println("closed")
    }
  }
}
