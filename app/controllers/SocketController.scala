package controllers

import models.{ClientCookie, Games, GameAction}
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.mvc._


class SocketController {

  def socket = WebSocket.using[GameAction] { request =>
    val cookies = request.cookies

    val nameCookie = ClientCookie.USER_NAME.getCookie(cookies)
    val idCookie = ClientCookie.USER_ID.getCookie(cookies)
    val gameDefIdCookie = ClientCookie.GAME_INSTANCE_ID.getCookie(cookies)
    val gameIdCookie = ClientCookie.GAME_ID.getCookie(cookies)

    Games.getGame(gameIdCookie.get.value, gameDefIdCookie.get.value).fold[(Iteratee[GameAction, _], Enumerator[GameAction])]((null, null))(game => {
      game.getClient(idCookie.get.value).get.connection()
    })
  }

}
