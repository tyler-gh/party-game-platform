package controllers

import controllers.traits.GameAssetController
import models.game.Games
import play.api.mvc._

class StyleGuideController(implicit games: Games) extends Controller with GameAssetController {

  def get = Action {
    Ok(views.html.styleguide())
  }

  def styleJs = Action {
    assetsSeqToString(games.style.outputJsMainClientFiles).map { assets =>
      Ok(assets).as("text/javascript")
    }.get
  }
}