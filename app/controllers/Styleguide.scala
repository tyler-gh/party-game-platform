package controllers

import play.api._
import play.api.db.DB
import play.api.mvc._

class Styleguide extends Controller {

  def get = Action {
    //DB.withConnection { conn =>
    //  
    //}

    Ok(views.html.styleguide())
  }

}