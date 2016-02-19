package controllers

import models.{ClientInfo, ClientCookie}
import models.game.Games
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

        Ok(clientJson.as[JsObject]).withCookies(
          ClientCookie.ACTIVE_GAME.createCookie(true),
          ClientCookie.USER_ID.createCookie(client.clientInfo.id),
          ClientCookie.GAME_INSTANCE_ID.createCookie(gameInstanceId),
          ClientCookie.GAME_ID.createCookie(gameId)
        )
      }
    }

    val gameInsCookieOpt = ClientCookie.GAME_INSTANCE_ID.getCookie(request.cookies)
    val gameIdCookieOpt = ClientCookie.GAME_ID.getCookie(request.cookies)
    val userIdCookieOpt = ClientCookie.USER_ID.getCookie(request.cookies)

    (gameInsCookieOpt, gameIdCookieOpt, userIdCookieOpt) match {
      case (Some(gameInstanceIdCookie), Some(gameIdCookie), Some(userIdCookie)) =>
        if(gameIdCookie.value.equals(gameId) && gameInstanceIdCookie.value.equals(gameInstanceId)) {
          Games.getGame(gameId, gameInstanceId).fold(newClient()){ game =>
            game.restoreClient(new ClientInfo(userIdCookie.value, userName, color)).fold(newClient())(client => Ok.withCookies(ClientCookie.ACTIVE_GAME.createCookie(true)))
          }
        } else {
          newClient()
        }
      case _ => newClient()
    }
  }

}
