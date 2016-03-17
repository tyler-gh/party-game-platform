package util

import java.io.{FileInputStream, FileOutputStream, File, IOException}
import java.nio.charset.StandardCharsets
import java.nio.file.StandardWatchEventKinds._
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.Executors
import akka.actor.ActorSystem

import akka.pattern.ask
import com.typesafe.jse.Engine.JsExecutionResult

import com.typesafe.jse.{Trireme, Engine}
import akka.util.Timeout
import models.game.{Games, GameDefinition}
import play.api.libs.json.Json
import scala.concurrent._
import scala.concurrent.duration._
import scala.collection.immutable
import FuncTransform._
import PGPLog._

class GameAssetsCompiler(games: Games) {

  private implicit lazy val context = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(((games.getGameDefinitions.size + 2) * 1.5).asInstanceOf[Int]))

  private lazy val watchers: Seq[GameWatcher] =
    games.getGameDefinitions.map(gameDef => new GameWatcher(gameDef)) :+
      new GameWatcher(games.style) :+
      new GameWatcher(games.lobby)

  def initialCompilation() = {
    watchers.foreach(watcher => watcher.initialCompilation())
  }

  def shutdown() = {
    watchers.foreach(watcher => watcher.shutdown())
  }


  def apply(): Unit = {
    watchers.foreach(watcher => Future {
      watcher()
    })
  }

}

sealed class GameWatcher(gameDef: GameDefinition)(implicit context: ExecutionContext) {

  private val watcher = FileSystems.getDefault.newWatchService()
  private val buildFile = new File("conf/build.js")

  def shutdown() {
    watcher.close()
  }

  private def compile(file: File) {

    if (file.toString.endsWith(".jsx")) {
      implicit val system = ActorSystem("jse-system")
      implicit val timeout = Timeout(500.seconds)
      val outputPath = gameDef.getOutputPath(file)
      println(s"compiling '$file' to '$outputPath'")
      new File(outputPath).getParentFile.mkdirs()
      val files = immutable.Seq(Json.toJson(Seq(file.getAbsolutePath)).toString(), Json.toJson(Seq(outputPath)).toString())
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
    } else if (file.toString.endsWith(".scss")) {
      gameDef.cssClientFiles.foreach(files => files.map(gameDef.getOutputPath).foreach(outputPath => {

        println(s"compiling '$file' to '$outputPath'")

        new File(outputPath).getParentFile.mkdirs()
        val compiler = new io.bit3.jsass.Compiler()
        val options = new io.bit3.jsass.Options()
        options.setOutputStyle(io.bit3.jsass.OutputStyle.COMPRESSED)
        try {
          gameDef.cssClientFiles.foreach(files => files.map(_.getAbsolutePath).foreach(file => {
            val css = compiler.compileFile(new File(file).toURI, new File("x").toURI, options)
            Files.write(new File(outputPath).toPath, css.getCss.getBytes(StandardCharsets.UTF_8))
          }))
        } catch {
          case e: io.bit3.jsass.CompilationException =>
            e.printStackTrace()
        }

      }))
    } else if (file.toString.endsWith(".js") || file.toString.endsWith(".css")) {
      val outputPath = gameDef.getOutputPath(file)
      println(s"moving '$file' to '$outputPath'")
      new File(outputPath).getParentFile.mkdirs()
      new FileOutputStream(outputPath) getChannel() transferFrom(new FileInputStream(file) getChannel, 0, Long.MaxValue)
    } else {
      s"Invalid File '$file'".printErrLn()
    }
  }

  private def walkPathFiles(path: Path, consumer: Path => Unit, dirConsumer: Option[Path => Unit] = None): Unit = {
    Files.walkFileTree(path, new SimpleFileVisitor[Path] {
      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        consumer(file)
        FileVisitResult.CONTINUE
      }

      override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
        dirConsumer.foreach(_ (dir))
        FileVisitResult.CONTINUE
      }
    })
  }

  private def walkLobbyFiles(files: Option[Seq[File]], consumer: String => Unit) {
    files.foreach(files => files.map(_.toString).filter(_.startsWith("games/lobby/")).foreach(consumer))
  }

  private def recompile(): Unit = {
    walkPathFiles(gameDef.path.toPath, path => {
      if (!path.toAbsolutePath.toString.endsWith("definition.yml")) {
        checkCompile(path)
      }
    })
    def compileShared(files: Option[Seq[File]]): Unit = {
      walkLobbyFiles(files, lobbyFile => checkCompile(new File(lobbyFile).toPath))
    }
    compileShared(gameDef.jsMainClientFiles)
    compileShared(gameDef.jsClientFiles)
    compileShared(gameDef.cssClientFiles)
  }

  def initialCompilation(): Unit = {
    new File(gameDef.path, "build").mkdir()
    walkPathFiles(gameDef.path.toPath, checkCompile, Some(dir => {
      if (!dir.getFileName.toString.equals("build")) {
        dir.toAbsolutePath.register(watcher, ENTRY_MODIFY)
      }
    }))

    def compileShared(files: Option[Seq[File]]): Unit = {
      walkLobbyFiles(files, { lobbyFile =>
        val file = new File(lobbyFile).toPath
        checkCompile(file)
        file.getParent.toAbsolutePath.register(watcher, ENTRY_MODIFY)
      })
    }
    compileShared(gameDef.jsMainClientFiles)
    compileShared(gameDef.jsClientFiles)
    compileShared(gameDef.cssClientFiles)
  }

  private def containsPath(filesOpt: Option[Seq[File]], path: String): Boolean = {
    filesOpt.exists(files => files.exists(file => {
      file.getAbsolutePath.equals(path)
    }))
  }

  private def checkCompile(path: Path): Unit = {
    val p = path.toAbsolutePath.toString
    if (p.endsWith("definition.yml")) {
      println("Reloading game definition")
      GameDefinition(gameDef)
      recompile()
    } else if (!p.contains("/build/")) {
      val shouldCompile = ((containsPath(gameDef.jsClientFiles, p) || containsPath(gameDef.jsMainClientFiles, p)) && (p.endsWith(".jsx") || p.endsWith(".js"))) ||
        ((p.endsWith(".scss") || p.endsWith(".css")) && gameDef.cssClientFiles.isDefined)

      if (shouldCompile) {
        compile(path.toAbsolutePath.toFile)
      }
    }
  }

  def apply(): Unit = {
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
        println("Closed Game Asset Compiler")
    }
  }

}
