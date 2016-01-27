package controllers

import models.Games
import play.api.libs.json.Json
import play.api.mvc._


class GameDefinitionsController extends Controller {

  def getDefinitions = Action {
    Ok(Json.toJson(Games.getGameDefinitions))
  }

}
