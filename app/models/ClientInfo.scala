package models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.WebSocket.FrameFormatter


object ClientInfo {
  implicit val clientInfoReads: Reads[ClientInfo] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "name").read[String]
    ) (ClientInfo.apply _)

  implicit val clientInfoWrites: Writes[ClientInfo] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "name").write[String]
    ) (unlift(ClientInfo.unapply))

  implicit val clientInfoFormat: Format[ClientInfo] = Format(clientInfoReads, clientInfoWrites)
  implicit val clientInfoFormatter = FrameFormatter.jsonFrame[ClientInfo]
}

case class ClientInfo(id: Long, name: String)
