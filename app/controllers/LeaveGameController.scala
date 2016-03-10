package controllers

import controllers.traits.CookieAuth
import models.ClientCookie
import models.game.Games
import play.api.mvc._

class LeaveGameController(implicit games: Games) extends Controller with CookieAuth[Result] {
  def leave = Action { implicit request =>
    auth((game, client) => {
      if(client.clientInfo.id == 0) {
        games.closeGame(game)
      } else {
        game.clientLeft(client)
      }
      Ok.withCookies(ClientCookie.ACTIVE_GAME.createCookie(false))
    })
  }
}
