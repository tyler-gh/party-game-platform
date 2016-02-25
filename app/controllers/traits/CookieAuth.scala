package controllers.traits

import models.{Client, ClientCookie}
import models.game.{Games, Game}
import play.api.mvc._

trait CookieAuth[T] {

  def auth(authorized: (Game, Client) => T, games: Games)(implicit request: RequestHeader, authError: AuthError[T]):T = {
    val cookies = request.cookies

    val activeGameCookieOpt = ClientCookie.ACTIVE_GAME.getCookie(cookies)
    val idCookieOpt = ClientCookie.USER_ID.getCookie(cookies)
    val gameIdCookieOpt = ClientCookie.GAME_ID.getCookie(cookies)
    val gameInstanceIdCookieOpt = ClientCookie.GAME_INSTANCE_ID.getCookie(cookies)

    (activeGameCookieOpt, idCookieOpt, gameInstanceIdCookieOpt, gameIdCookieOpt) match {
      case (Some(activeGameCookie), Some(idCookie), Some(gameInstanceIdCookie), Some(gameIdCookie)) =>
        if(activeGameCookie.value) {
          games.getGame(gameIdCookie.value, gameInstanceIdCookie.value).fold(authError.gameDNE()) { gameInstance =>
            gameInstance.getClient(idCookie.value).fold(authError.notAMemberOfGame()) { client =>
              authorized(gameInstance, client)
            }
          }
        } else {
          authError.leftGame()
        }
      case _ =>
        authError.notAMemberOfGame()
    }
  }
}
