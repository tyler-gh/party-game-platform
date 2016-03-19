package models.game

import java.io.{FileInputStream, File}
import java.nio.file.Path
import java.util

import org.yaml.snakeyaml.Yaml
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath, Reads, Writes}
import play.api.mvc.WebSocket.FrameFormatter

import scala.collection.JavaConverters._


object GameDefinitionInfo {
  implicit val gameDefinitionReads: Reads[GameDefinitionInfo] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "title").read[String] and
      (JsPath \ "color").read[String] and
      (JsPath \ "description").read[String]
    ) (GameDefinitionInfo.apply _)

  implicit val gameDefinitionWrites: Writes[GameDefinitionInfo] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "title").write[String] and
      (JsPath \ "color").write[String] and
      (JsPath \ "description").write[String]
    ) (unlift(GameDefinitionInfo.unapply))

  implicit val gameDefinitionFormat: Format[GameDefinitionInfo] = Format(gameDefinitionReads, gameDefinitionWrites)
  implicit val gameDefinitionFormatter = FrameFormatter.jsonFrame[GameDefinitionInfo]
}

case class GameDefinitionInfo(id: String, title: String, color: String, description: String)

case class GameDefinition(
  path: File,
  info: GameDefinitionInfo,
  jsServerFile: Option[File],
  // these should only be vars for development purposes. There might be a better way to do this ...
  var jsClientFiles: Option[Seq[File]],
  var jsMainClientFiles: Option[Seq[File]],
  var cssClientFiles: Option[Seq[File]]
) {

  def outputJsClientFiles = jsClientFiles.map(_.map(s => new File(getOutputPath(s))))
  def outputJsMainClientFiles = jsMainClientFiles.map(_.map(s => new File(getOutputPath(s))))
  def outputCssClientFiles = cssClientFiles.map(_.map(s => new File(getOutputPath(s))))

  def getOutputPath(file: File): String = {
    getOutputPath(file.getAbsolutePath)
  }

  def getOutputPath(path: Path): String = {
    getOutputPath(path.toAbsolutePath.toString)
  }

  def getOutputPath(path: String): String = {
    val basePath = this.path.getParentFile.getAbsolutePath
    val folderPath = path.substring(basePath.length)
    val filePath = folderPath.substring(folderPath.indexOf(File.separator))
    this.path.getAbsolutePath + "/build" + replaceOutputExtension(filePath)
  }

  private def replaceOutputExtension(path: String):String = {
    val pathWithExt = path.split('.')
    val newExtension = pathWithExt.last match {
      case "scss" => "css"
      case "jsx" => "js"
      case ext => ext
    }
    pathWithExt.init :+ newExtension mkString "."
  }

}

object GameDefinition {

  def apply(gameDefinition: GameDefinition) {
    gameDefinition.synchronized {
      implicit val folder = gameDefinition.path
      implicit val gameValues = loadYaml(new File(gameDefinition.path, "definition.yml"))
      gameDefinition.jsClientFiles = getOptionalList("js_client_files")
      gameDefinition.jsMainClientFiles = getOptionalList("js_main_client_files")
      gameDefinition.cssClientFiles = getOptionalList("css_client_files")
    }
  }

  def apply(implicit folder: File): GameDefinition = {
    implicit val gameValues = loadYaml(new File(folder, "definition.yml"))
    new GameDefinition(
      folder,
      new GameDefinitionInfo(
        getMapValue("id").get,
        getMapValue("title").get,
        getMapValue("color").get,
        getMapValue("description").get
      ), getMapValue("js_server_file", Some((file: String) => new File(folder, file))),
      getOptionalList("js_client_files"),
      getOptionalList("js_main_client_files"),
      getOptionalList("css_client_files")
    )
  }

  private def loadYaml(file: File): util.Map[String, AnyRef] = {
    new Yaml().load(new FileInputStream(file)).asInstanceOf[util.Map[String, AnyRef]]
  }

  private def getOptionalList(key: String)(implicit map: util.Map[String, AnyRef], folder: File): Option[Seq[File]] = {
    getMapValue[util.ArrayList[String], Seq[File]](key, Some(_.asScala.map { s =>
      val (parent:File,file:String) = if(s.startsWith("../lobby/")) {
        (folder.getParentFile, s.substring(3))
      } else {
        (folder,s)
      }
      new File(parent, file)
    }))
  }

  private def getMapValue[T, R](key: String, transform: Option[T => R] = None)(implicit map: util.Map[String, AnyRef]): Option[R] = {
    Option(map.get(key)).map(value => transform.fold(value.asInstanceOf[R])(f => f(value.asInstanceOf[T])))
  }
}
