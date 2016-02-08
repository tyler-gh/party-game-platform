package controllers

import models.ClientCookie
import models.game.Games
import play.api.mvc._
import play.api.libs.json._

class CreateGameController extends Controller {

  def create(gameIdOpt: Option[String]) = Action { request =>
    gameIdOpt.orElse[String](request.body.asJson.flatMap[String](json => (json \ "game_id").asOpt[String])).fold[Result](BadRequest) { gameId =>
      Games.createGame(gameId).fold(NotFound(Json.obj("error" -> "Game with id %s was not found".format(gameId)))) { game =>
        val client = game.addClient("root", "black") // TODO: what client info should root have?
        Ok(Json.obj("game_instance_id" -> game.id, "game_id" -> gameId, "user_name" -> client.clientInfo.name, "user_id" -> client.clientInfo.id)).withCookies(
          ClientCookie.USER_NAME.createCookie(client.clientInfo.name),
          ClientCookie.USER_ID.createCookie(client.clientInfo.id),
          ClientCookie.GAME_INSTANCE_ID.createCookie(game.id),
          ClientCookie.GAME_ID.createCookie(gameId)
        )
      }
    }
  }
}
