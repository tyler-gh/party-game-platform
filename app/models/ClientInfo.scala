package models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.WebSocket.FrameFormatter


object ClientInfo {
  implicit val clientInfoReads: Reads[ClientInfo] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "color").read[String]
    ) (ClientInfo.apply _)

  implicit val clientInfoWrites: Writes[ClientInfo] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "color").write[String]
    ) (unlift(ClientInfo.unapply))

  implicit val clientInfoFormat: Format[ClientInfo] = Format(clientInfoReads, clientInfoWrites)
  implicit val clientInfoFormatter = FrameFormatter.jsonFrame[ClientInfo]
}

case class ClientInfo(id: Int, name: String, color: String)
