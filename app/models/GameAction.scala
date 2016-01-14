package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.mvc.WebSocket.FrameFormatter

object GameAction {

  sealed abstract class Type(val id: Int, val name: String)

  case object NEW_CLIENT extends Type(0, "new-client")

  case object GROUP_MESSAGE extends Type(1, "group-message")

  val actions = Seq(NEW_CLIENT, GROUP_MESSAGE)

  def withName(s: String): Option[Type] = {
    actions.find(action => action.name.equals(s))
  }

  implicit val gameActionReads: Reads[GameAction] = (
    (JsPath \ "client").read[ClientInfo] and
    (JsPath \ "actionType").read[String].map(s => withName(s).get) and // TODO: this get will throw an error if Type DNE
      (JsPath \ "data").readNullable[JsValue]
    ) (GameAction.apply _)

  implicit val gameActionWrites: Writes[GameAction] = (
    (JsPath \ "client").write[ClientInfo] and
      (JsPath \ "actionType").write[GameAction.Type](new Writes[GameAction.Type] {
        def writes(v: GameAction.Type): JsValue = JsString(v.name)
      }) and
      (JsPath \ "data").writeNullable[JsValue]
    ) (unlift(GameAction.unapply))

  implicit val gameActionFormat: Format[GameAction] = Format(gameActionReads, gameActionWrites)
  implicit val actionFormatter = FrameFormatter.jsonFrame[GameAction]
}

case class GameAction(clientInfo: ClientInfo, gameActionType: GameAction.Type, data: Option[JsValue]);
