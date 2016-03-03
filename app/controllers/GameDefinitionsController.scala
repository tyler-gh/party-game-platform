package controllers

import models.game.Games
import play.api.libs.json.{JsString, Json}
import play.api.mvc._


class GameDefinitionsController(games: Games) extends Controller {

  def getDefinitions = Action {
    Ok(Json.toJson(games.getGameDefinitionsInfo))
  }

}
