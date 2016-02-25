package models

import models.game.{ClientAction, PGPAction, GameAction}
import play.api.libs.iteratee.{Enumerator, Concurrent, Iteratee}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try


abstract class SocketActor() {

  private val (out, channel) = Concurrent.broadcast[PGPAction]
  private val in = Iteratee.foreach[PGPAction] { msg =>
    onAction(msg)
  } map { _ =>
    onClose()
  }

  def refs:(Iteratee[PGPAction, _], Enumerator[PGPAction]) = {
    (in, out)
  }

  def send(action: GameAction) {
    try {
      channel.push(action)
    } catch {
      case scala.util.control.NonFatal(e) =>
        onClose()
    }
  }

  def close() {
    Try {
      channel.eofAndEnd()
    }
    onClose()
  }

  def onAction(action: PGPAction)
  def onClose()

}

