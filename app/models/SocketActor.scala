package models

import play.api.libs.iteratee.{Concurrent, Iteratee}
import scala.concurrent.ExecutionContext.Implicits.global


abstract class SocketActor() {

  private final val (out, channel) = Concurrent.broadcast[String]
  private final val in = Iteratee.foreach[String] { msg =>
    onMessage(msg)
  } map { _ =>
    onClose()
  }

  def refs = (in, out)

  def send(msg: String) {
    channel.push(msg)
  }

  def onMessage(msg: String)
  def onClose()

}
