package controllers

import models.ClientCookie
import play.api.mvc._

/**
  *
  */
class LeaveGameController extends Controller {
  def leave = Action { implicit request =>
    Ok.withCookies(ClientCookie.ACTIVE_GAME.createCookie(false))
  }
}
