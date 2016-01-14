package controllers

import models.{Games, GameAction}
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.mvc._


class SocketController {

  def socket(name: String, game: String, id: Long) = WebSocket.using[GameAction] { request =>
      Games.getGame(game).fold[(Iteratee[GameAction, _], Enumerator[GameAction])]((null, null))(game => {
        game.getClient(id).get.connection()
      })
  }

}
