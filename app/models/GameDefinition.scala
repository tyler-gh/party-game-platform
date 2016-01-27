package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, Writes, JsPath, Reads}
import play.api.mvc.WebSocket.FrameFormatter


object GameDefinition {
  implicit val gameDefinitionReads: Reads[GameDefinition] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "title").read[String] and
      (JsPath \ "color").read[String]
    ) (GameDefinition.apply _)

  implicit val gameDefinitionWrites: Writes[GameDefinition] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "title").write[String] and
      (JsPath \ "color").write[String]
    ) (unlift(GameDefinition.unapply))

  implicit val gameDefinitionFormat: Format[GameDefinition] = Format(gameDefinitionReads, gameDefinitionWrites)
  implicit val gameDefinitionFormatter = FrameFormatter.jsonFrame[ClientInfo]
}

case class GameDefinition(id: String, title: String, color: String)
