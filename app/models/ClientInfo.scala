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

  // todo This is temporary until we can come up with a better solution
  private val colorMap = collection.immutable.HashMap(
    "color1-button" ->  "#4A90E2",
    "color2-button" ->  "#7BBC5D",
    "color3-button" ->  "#7849A8",
    "color4-button" ->  "#6DB191",
    "color5-button" ->  "#ECBC3D",
    "color6-button" ->  "#D0021B",
    "color7-button" ->  "#174881",
    "color8-button" ->  "#851C77",
    "color9-button" ->  "#DF487D",
    "color10-button" -> "#5594AF",
    "color11-button" -> "#FF8100",
    "color12-button" -> "#7A858D"
  )
}

case class ClientInfo(id: Int, name: String, color: String) {
  def colorCode = {
    ClientInfo.colorMap.getOrElse(color, "#000000")
  }
}


