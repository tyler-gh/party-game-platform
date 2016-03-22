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
    watchers.foreach(_.initialCompilation())
  }

  def shutdown() = {
    watchers.foreach(_.shutdown())
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

  private def compileIfNeeded(file: File, checkFile: File, outputPath: String, compiler: (File, File) => Unit): Unit = {
    val outputFile = new File(outputPath)
    if (!outputFile.exists() ||  outputFile.lastModified() < checkFile.lastModified()) {
      outputFile.getParentFile.mkdirs()
      println(s"compiling '$file' to '$outputPath'")
      compiler(file.getAbsoluteFile, outputFile)
    }
  }

  private def compileIfNeeded(file: File, compiler: (File, File) => Unit): Unit = {
    compileIfNeeded(file, file, gameDef.getOutputPath(file), compiler)
  }

  private def compileJsx(file: File, outputFile: File): Unit = {
    implicit val system = ActorSystem("jse-system")
    implicit val timeout = Timeout(500.seconds)
    val files = immutable.Seq(Json.toJson(Seq(file.getAbsolutePath)).toString(), Json.toJson(Seq(outputFile.getAbsolutePath)).toString())
    val future = system.actorOf(Trireme.props(), "engine").ask(Engine.ExecuteJs(buildFile, files, timeout.duration))
    future.onComplete(result => {
      val jsResult = result.get.asInstanceOf[JsExecutionResult]
      if (jsResult.exitValue != 0) {
        jsResult.printResult()
      }
    })
    Await.result(future, timeout.duration)
    system.shutdown()
  }

  private def compileSass(file: File, outputFile: File): Unit = {
    val compiler = new io.bit3.jsass.Compiler()
    val options = new io.bit3.jsass.Options()
    options.setOutputStyle(io.bit3.jsass.OutputStyle.COMPRESSED)
    try {
      val css = compiler.compileFile(file.toURI, new File("x").toURI, options)
      Files.write(outputFile.toPath, css.getCss.getBytes(StandardCharsets.UTF_8))
    } catch {
      case e: io.bit3.jsass.CompilationException =>
        e.printStackTrace()
    }
  }

  private def copyFile(file: File, outputFile: File): Unit = {
    new FileOutputStream(outputFile) getChannel() transferFrom(new FileInputStream(file) getChannel, 0, Long.MaxValue)
  }

  private def ext(file: File): String = {
    val i = file.getName.lastIndexOf('.')
    if (i > 0) {
      file.getName.substring(i+1)
    } else {
      ""
    }
  }

  private def compile(file: File) {
    ext(file) match {
      case "jsx" => compileIfNeeded(file, compileJsx)
      case "scss" => gameDef.cssClientFiles.foreach(files =>
        files.foreach(compileFile => compileIfNeeded(compileFile, file, gameDef.getOutputPath(compileFile), compileSass))
      )
      case "js" | "css" => compileIfNeeded(file, copyFile)
      case default => s"Invalid File '$file' with extension '${ext(file)}'".printErrLn()
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
    files.foreach(files => files.map(_.toString).filter(_.startsWith("games" + File.separator + "lobby" + File.separator)).foreach(consumer))
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
    } else if (!p.contains(File.separator +  "build" + File.separator)) {
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
