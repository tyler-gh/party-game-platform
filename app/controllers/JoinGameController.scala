package controllers

import models.{ClientCookie, Games}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._


class JoinGameController extends Controller {

  case class JoinGameParams(userName: String, gameId: String, gameInstanceId: String, color: String)
  implicit val joinGameReads: Reads[JoinGameParams] = (
    (JsPath \ "user_name").read[String] and
      (JsPath \ "game_id").read[String] and
      (JsPath \ "game_instance_id").read[String] and
      (JsPath \ "color").read[String]
    ) (JoinGameParams.apply _)

  def join(userNameOpt: Option[String], gameIdOpt: Option[String], gameInstanceIdOpt: Option[String], colorOpt: Option[String]) = Action { implicit request =>
    (userNameOpt, gameIdOpt, gameInstanceIdOpt, colorOpt) match {
      case (Some(userName), Some(gameId), Some(gameInstanceId), Some(color)) =>
        joinGame(userName, gameInstanceId, gameId, color)
      case _ =>
        request.body.asJson.flatMap(_.asOpt[JoinGameParams]).fold[Result](BadRequest) { params =>
          joinGame(params.userName, params.gameInstanceId, params.gameId, params.color)
        }
    }
  }

  def joinGame(userName: String, gameInstanceId: String, gameId: String, color: String)(implicit request: Request[Any]) = {

    def newClient() = {
      Games.getGame(gameId, gameInstanceId).fold[Result](BadRequest) { game =>
        val client = game.addClient(userName, color)
        val clientJson = Json.toJson(client.clientInfo)

        // TODO load previous actions
        Ok(clientJson.as[JsObject] + ("users" -> Json.toJson(game.allClients))).withCookies(
          ClientCookie.USER_NAME.createCookie(userName),
          ClientCookie.USER_ID.createCookie(client.clientInfo.id),
          ClientCookie.GAME_INSTANCE_ID.createCookie(gameInstanceId),
          ClientCookie.GAME_ID.createCookie(gameId)
        )
      }
    }

    (ClientCookie.GAME_INSTANCE_ID.getCookie(request.cookies), ClientCookie.GAME_ID.getCookie(request.cookies)) match {
      case (Some(gameInstanceIdCookie), Some(gameIdCookie)) =>
        if(gameIdCookie.value.equals(gameId) && gameInstanceIdCookie.value.equals(gameInstanceId)) {
          // TODO: check to make sure the game still exists
          Ok
        } else {
          newClient()
        }
      case _ => newClient()
    }
  }

}
