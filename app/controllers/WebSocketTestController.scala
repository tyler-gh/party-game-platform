package controllers

import play.api.mvc._

class WebSocketTestController extends Controller {

  def get = Action {
    Ok(views.html.websocket_test_view())
  }

}
