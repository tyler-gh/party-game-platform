package models.game

import java.io.File

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath, Reads, Writes}
import play.api.mvc.WebSocket.FrameFormatter


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
  info: GameDefinitionInfo,
  jsServerFile: Option[File],
  jsClientFiles: Option[Seq[File]],
  jsMainClientFiles: Option[Seq[File]]
)
