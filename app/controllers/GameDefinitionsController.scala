package controllers

import javax.inject.Inject

import models.game.Games
import play.api.libs.json.Json
import play.api.mvc._


class GameDefinitionsController @Inject()(games: Games) extends Controller {

  def getDefinitions = Action {
    Ok(Json.toJson(games.getGameDefinitionsInfo))
  }

}
