package controllers

import controllers.traits.CookieAuth
import play.api.mvc._

class Application extends Controller with CookieAuth[Result] {

  def index = Action { implicit request =>
    auth({ (game, client) =>
      Redirect(routes.GameController.get)
    }, { () =>
      Ok(views.html.index("PGP"))
    })
  }

}
