package controllers

import models.SocketActor
import play.api.mvc._

class SocketController {

  val actors = scala.collection.mutable.ArrayBuffer.empty[SocketActor]

  def socket = WebSocket.using[String] { request =>
    val actor = new SocketActor() {
      override def onMessage(msg: String) {
        actors.foreach(a => a.send(msg))
      }
      override def onClose(): Unit = {
        actors -= this
      }
    }
    actors += actor
    actor.refs
  }

}
