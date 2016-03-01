package controllers

import javax.inject.Inject

import controllers.traits.{FlatAuthError, CookieAuth}
import models.game.Games
import play.api.mvc._


class GameController @Inject()(games: Games) extends Controller with CookieAuth[Result] {

  def get = Action { implicit request =>
    auth({ (game, client) =>
      val gameDef = game.gameDef
      Ok(views.html.game(gameDef.info.id, gameDef.info.title, gameDef.info.description, game.id, None, None))
    }, games)(request, new FlatAuthError[Result](Redirect(routes.IndexController.index)))
  }
}
