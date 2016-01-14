package models

import play.api.libs.iteratee.{Concurrent, Iteratee}
import scala.concurrent.ExecutionContext.Implicits.global


abstract class SocketActor() {

  private final val (out, channel) = Concurrent.broadcast[GameAction]
  private final val in = Iteratee.foreach[GameAction] { msg =>
    onAction(msg)
  } map { _ =>
    onClose()
  }

  def refs = (in, out)

  def send(action: GameAction) {
    channel.push(action)
  }

  def close() {
    channel.eofAndEnd()
    onClose()
  }

  def onAction(action: GameAction)
  def onClose()

}
