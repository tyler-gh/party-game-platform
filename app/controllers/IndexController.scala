package controllers

import controllers.traits.{FlatAuthError, CookieAuth}
import models.game.Games
import play.api.mvc._

class IndexController(games: Games) extends Controller with CookieAuth[Result] {

  def index = Action { implicit request =>
    auth(games, { (game, client) =>
      Redirect(routes.GameController.get)
    })(request, new FlatAuthError[Result](Ok(views.html.index("PGP"))))
  }

}
