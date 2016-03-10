package controllers

import controllers.traits.{GameAssetController, FlatAuthError, CookieAuth}
import models.game.Games
import play.api.libs.json.{JsString, Json}
import play.api.mvc._

class IndexController(implicit games: Games) extends Controller with CookieAuth[Result] with GameAssetController {

  implicit val authError = new FlatAuthError[Result](Ok(views.html.index("PGP")))

  def index = Action { implicit request =>
    auth({ (game, client) =>
      Redirect(routes.GameController.get())
    })
  }

  def lobbyJs = Action {
    assetsSeqToString(games.lobby.outputJsMainClientFiles).map { assets =>
      Ok(assets).as("text/javascript")
    }.get
  }

  def gameJs = Action {
    assetsSeqToString(games.lobby.outputJsClientFiles).map { assets =>
      Ok(assets).as("text/javascript")
    }.get
  }

  def lobbyCss = Action {
    assetsSeqToString(games.lobby.outputCssClientFiles).map { assets =>
      Ok(assets).as("text/css")
    }.get
  }

}
