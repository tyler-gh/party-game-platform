package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.mvc.WebSocket.FrameFormatter

object GameAction {

  sealed class Type(val id: Int, val name: String)

  case object NEW_CLIENT extends Type(0, "new-client")

  // TODO start game action, used to enforce not joining after the game has started

  case object UNFORMED extends Type(1, "")

  val actions = Seq(NEW_CLIENT, UNFORMED)

  def withName(s: String): Type = {
    actions.find(action => action.name.equals(s)).getOrElse(new Type(UNFORMED.id, s))
  }

  implicit val gameActionReads: Reads[GameAction] = (
    (JsPath \ "client").read[ClientInfo] and
    (JsPath \ "actionType").read[String].map(s => withName(s)) and
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
