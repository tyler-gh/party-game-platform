package controllers

import controllers.traits.CookieAuth
import play.api.mvc._


class GameController extends Controller with CookieAuth[Result] {

  def get = Action { implicit request =>
    auth({ (game, client) =>
      val gameDef = game.gameDef
      Ok(views.html.game(gameDef.info.id, gameDef.info.title, gameDef.info.description, game.id, None, None))
    }, { () =>
      Redirect(routes.Application.index)
    })
  }
}
