package controllers

import models.{ClientCookie, Games, GameAction}
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.mvc._


class SocketController {

  def socket = WebSocket.using[GameAction] { request =>
    val cookies = request.cookies

    val nameCookie = ClientCookie.NAME.getCookie(cookies)
    val gameCookie = ClientCookie.GAME.getCookie(cookies)
    val idCookie = ClientCookie.ID.getCookie(cookies)

    Games.getGame(gameCookie.get.value).fold[(Iteratee[GameAction, _], Enumerator[GameAction])]((null, null))(game => {
      game.getClient(idCookie.get.value).get.connection()
    })
  }

}
