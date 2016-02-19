package controllers.traits

import models.{Client, ClientCookie}
import models.game.{Game, Games}
import play.api.mvc._

trait CookieAuth[T] {
  def auth(authorized: (Game, Client) => T, neg:()=>T)(implicit request: RequestHeader):T = {
    val cookies = request.cookies

    val activeGameCookieOpt = ClientCookie.ACTIVE_GAME.getCookie(cookies)
    val idCookieOpt = ClientCookie.USER_ID.getCookie(cookies)
    val gameIdCookieOpt = ClientCookie.GAME_ID.getCookie(cookies)
    val gameInstanceIdCookieOpt = ClientCookie.GAME_INSTANCE_ID.getCookie(cookies)

    (activeGameCookieOpt, idCookieOpt, gameInstanceIdCookieOpt, gameIdCookieOpt) match {
      case (Some(activeGameCookie), Some(idCookie), Some(gameInstanceIdCookie), Some(gameIdCookie)) =>
        if(activeGameCookie.value) {
          Games.getGame(gameIdCookie.value, gameInstanceIdCookie.value).fold(neg()) { gameInstance =>
            gameInstance.getClient(idCookie.value).fold(neg()) { client =>
              authorized(gameInstance, client)
            }
          }
        } else {
          neg()
        }
      case _ =>
        neg()
    }
  }
}
