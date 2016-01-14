package controllers

import models.{Game, GameAction}
import play.api.mvc._


class SocketController {

  val game = new Game()

  def socket = WebSocket.using[GameAction] { request =>
    val nameOpt = request.getQueryString("name")
//    nameOpt.fold(() => {
//      throw new Exception
//    })(name => {
//      game.addClient(name).connection()
//    })
    println(nameOpt)
    game.addClient(nameOpt.get).connection()
  }

}
