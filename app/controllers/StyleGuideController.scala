package controllers

import play.api.mvc._

class StyleGuideController extends Controller {

  def get = Action {
    Ok(views.html.styleguide())
  }

}