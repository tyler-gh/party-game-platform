package controllers

import javax.inject.Inject

import models.{ClientInfo, ClientCookie}
import models.game.Games
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._


class JoinGameController @Inject()(games: Games) extends Controller {

  case class JoinGameParams(userName: String, gameId: String, gameInstanceId: String, color: String)
  implicit val joinGameReads: Reads[JoinGameParams] = (
    (JsPath \ "user_name").read[String] and
      (JsPath \ "game_id").read[String] and
      (JsPath \ "game_instance_id").read[String] and
      (JsPath \ "color").read[String]
    ) (JoinGameParams.apply _)

  def error(e: JsValue): Result = {
    BadRequest(Json.obj(("error",e)))
  }

  def join(userNameOpt: Option[String], gameIdOpt: Option[String], gameInstanceIdOpt: Option[String], colorOpt: Option[String]) = Action { implicit request =>
    (userNameOpt, gameIdOpt, gameInstanceIdOpt, colorOpt) match {
      case (Some(userName), Some(gameId), Some(gameInstanceId), Some(color)) =>
        joinGame(userName, gameInstanceId, gameId, color)
      case _ =>
        request.body.asJson.map(_.validate[JoinGameParams]).fold[Result](error(JsString("Missing required parameters"))) {
          case success: JsSuccess[JoinGameParams] =>
            val params = success.get
            joinGame(params.userName, params.gameInstanceId, params.gameId, params.color)
          case e: JsError =>
            error(JsError.toJson(e))
        }
    }
  }

  def joinGame(userName: String, gameInstanceId: String, gameId: String, color: String)(implicit request: Request[Any]) = {

    def newClient() = {
      games.getGame(gameId, gameInstanceId).fold[Result](error(JsString(s"Game with id '$gameId' and instance_id '$gameInstanceId' DNE"))) { game =>
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
          games.getGame(gameId, gameInstanceId).fold(newClient()){ game =>
            game.restoreClient(new ClientInfo(userIdCookie.value, userName, color)).fold(newClient())(client => Ok.withCookies(ClientCookie.ACTIVE_GAME.createCookie(true)))
          }
        } else {
          newClient()
        }
      case _ => newClient()
    }
  }

}
