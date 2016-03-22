package models.game

import models.ClientInfo
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.WebSocket.FrameFormatter

object GameAction {

  sealed class Type(val id: Int, val name: String)

  case object CLIENT_LEFT extends Type(1, "client-left")

  case object CLIENT_JOINED extends Type(2, "client-joined")

  case object GAME_STARTED extends Type(3, "game-started")

  case object CLIENT_DROPPED extends Type(4, "client-dropped")

  case object UNFORMED extends Type(Int.MaxValue, "")

  val actions = Seq(CLIENT_LEFT, CLIENT_JOINED, GAME_STARTED, CLIENT_DROPPED, UNFORMED)

  def withName(s: String): Type = {
    actions.find(action => action.name.equals(s)).getOrElse(new Type(UNFORMED.id, s) {
      override def toString: String = {
        name
      }
    })
  }

  implicit val clientActionReads: Reads[ClientAction] = (
    (JsPath \ "actionType").read[String].map(s => withName(s)) and
      (JsPath \ "data").readNullable[JsValue]
    ) (ClientAction.apply _)

  implicit val gameActionReads: Reads[GameAction] = (
    (JsPath \ "client").read[ClientInfo] and
      (JsPath \ "actionNumber").read[Int] and
      (JsPath \ "actionType").read[String].map(s => withName(s)) and
      (JsPath \ "data").readNullable[JsValue]
    ) (GameAction.apply _)

  implicit val gameActionWrites: Writes[GameAction] = (
    (JsPath \ "client").write[ClientInfo] and
      (JsPath \ "actionNumber").write[Int] and
      (JsPath \ "actionType").write[GameAction.Type](new Writes[GameAction.Type] {
        def writes(v: GameAction.Type): JsValue = JsString(v.name)
      }) and
      (JsPath \ "data").writeNullable[JsValue]
    ) (unlift(GameAction.unapply))
}

object PGPAction {
  implicit val pgpActionFormat: Format[PGPAction] = Format(new Reads[PGPAction] {
    override def reads(json: JsValue): JsResult[PGPAction] = {
      GameAction.clientActionReads.reads(json)
    }
  }, new Writes[PGPAction] {
    override def writes(action: PGPAction): JsValue = {
      action match {
        case gameAction: GameAction => GameAction.gameActionWrites.writes(gameAction)
        case _ => JsObject(Seq("error" -> JsString("PGPAction only serializes Game Actions with client info and action numbers")))
      }
    }
  })
  implicit val pgpActionFormatter = FrameFormatter.jsonFrame[PGPAction]
}

trait PGPAction {
  def gameActionType: GameAction.Type

  def data: Option[JsValue]
}

case class ClientAction(gameActionType: GameAction.Type, data: Option[JsValue]) extends PGPAction

case class GameAction(clientInfo: ClientInfo, actionNumber: Int, gameActionType: GameAction.Type, data: Option[JsValue]) extends PGPAction;
