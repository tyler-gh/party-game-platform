package controllers

import play.api._
import play.api.db.DB
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    //DB.withConnection { conn =>
    //  
    //}

    Ok(views.html.index("PGP"))
  }

}
