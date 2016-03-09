package util

import java.io.{File, IOException}
import java.nio.charset.StandardCharsets
import java.nio.file.StandardWatchEventKinds._
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import akka.actor.ActorSystem

import akka.pattern.ask
import com.typesafe.jse.Engine.JsExecutionResult

import com.typesafe.jse.{Trireme, Engine}
import akka.util.Timeout
import models.game.GameDefinition
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable
import util.FuncTransform._
import PGPLog._

class GameAssetsCompiler(games: Seq[GameDefinition]) {

  private val watchers: Seq[GameWatcher] = games.map(gameDef => new GameWatcher(gameDef))

  def shutdown() = {
    watchers.foreach(watcher => watcher.shutdown())
  }


  def apply(): Unit = {
    watchers.foreach(_ ())
  }

}

sealed class GameWatcher(gameDef: GameDefinition) {
  private val watcher = FileSystems.getDefault.newWatchService()
  private val buildFile = new File("conf/build.js")

  def shutdown() {
    watcher.close()
  }


  private def compile(file: String) {
    if (file.endsWith(".jsx")) {
      implicit val system = ActorSystem("jse-system")
      implicit val timeout = Timeout(5.seconds)

      val files = immutable.Seq(Json.toJson(Seq(file)).toString())
      val future = system.actorOf(Trireme.props(), "engine").ask(Engine.ExecuteJs(buildFile, files, timeout.duration))
      future.onComplete(result => {
        val jsResult = result.get.asInstanceOf[JsExecutionResult]
        if (jsResult.exitValue != 0) {
          new String(jsResult.output.toArray, "UTF-8").printErrLn()
          new String(jsResult.error.toArray, "UTF-8").printErrLn()
        }
      })
      Await.result(future, timeout.duration)
      system.shutdown()
    } else if (file.endsWith(".scss")) {
      val compiler = new io.bit3.jsass.Compiler()
      val options = new io.bit3.jsass.Options()
      options.setOutputStyle(io.bit3.jsass.OutputStyle.COMPRESSED)
      try {
        gameDef.cssClientFiles.foreach(files => files.map(_.getAbsolutePath).foreach(file => {
          val css = compiler.compileFile(new File(file).toURI, new File("x").toURI, options)
          Files.write(new File(file.replace(".scss", ".css")).toPath, css.getCss.getBytes(StandardCharsets.UTF_8))
        }))
      } catch {
        case e: io.bit3.jsass.CompilationException =>
          e.printStackTrace()
      }
    } else {
      s"Invalid File '$file'".printErrLn()
    }
  }

  private def containsPath(filesOpt: Option[Seq[File]], path: String): Boolean = {
    filesOpt.exists(files => files.exists(file => file.getAbsolutePath.equals(path)))
  }

  private def checkCompile(path: Path): Unit = {
    val p = path.toAbsolutePath.toString
    val shouldCompile = ((
      containsPath(gameDef.jsClientFiles, p) ||
        containsPath(gameDef.jsMainClientFiles, p)
      ) && p.endsWith(".jsx")) ||
      (p.endsWith(".scss") && gameDef.cssClientFiles.isDefined)

    if (shouldCompile) {
      println(s"compiling '$p'")
      compile(p)
    }
  }

  def apply(): Unit = {

    Files.walkFileTree(gameDef.path.toPath, new SimpleFileVisitor[Path] {
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
      while (true) {
        val key = watcher.take()
        key.pollEvents().forEach((event: WatchEvent[_]) => {
          checkCompile(key.watchable().asInstanceOf[Path].resolve(event.context().asInstanceOf[Path]))
        })
        key.reset()
      }
    } catch {
      case x@(_: IOException | _: ClosedWatchServiceException) =>
        println("closed")
    }
  }

}
