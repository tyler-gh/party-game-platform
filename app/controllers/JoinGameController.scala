package controllers

import models.Games
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

}
