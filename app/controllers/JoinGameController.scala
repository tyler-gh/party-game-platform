package controllers

import models.{ClientCookie, Games}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._

/**
  *
  */
class JoinGameController extends Controller {

  var gameId: Long = 1024

  def view = Action {
    Ok(views.html.join_game("Join Game"))
  }

  def join(name: String, gameId: String) = Action {

    val game = Games.getGame(gameId).get
    val client = game.addClient(name)

    Ok(views.html.game("Game", name, client.clientInfo.id, gameId))
  }

  def join(userName: String, gameName: String, gameId: String) = Action { implicit request =>
    joinGame(userName, gameName, gameId)
  }

  def joinGame(userName: String, gameName: String, gameId: String)(implicit request: Request[Any]) = {

    def newClient() = {
      val game = Games.getGame(gameId).get
      val client = game.addClient(userName)
      val clientJson = Json.toJson(client.clientInfo)

      // TODO load previous actions
      Ok(clientJson.as[JsObject] + ("users" -> Json.toJson(game.allClients))).withCookies(
        ClientCookie.NAME.createCookie(userName),
        ClientCookie.GAME.createCookie(game.id),
        ClientCookie.ID.createCookie(client.clientInfo.id)
      )
    }

    // Option either None | Some(Cookie)

    ClientCookie.GAME.getCookie(request.cookies).fold(newClient()){ gameCookie =>
      if(gameCookie.value.equals(gameId)) {
        // TODO load previous actions
        Ok
      } else {
        newClient()
      }
    }
  }

}
