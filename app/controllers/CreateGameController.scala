package controllers

import models.Games
import play.api.mvc._

/**
  *
  */
class CreateGameController extends Controller {

  var gameId: Long = 1024

  def create = Action {

    val game = Games.createGame(gameId.toHexString).get
    gameId += 1

    Ok(views.html.game_details("Game Code", game.id))
  }

}
