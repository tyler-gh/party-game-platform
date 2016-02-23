package models

import models.game.{ClientAction, PGPAction, GameAction}
import play.api.libs.iteratee.{Enumerator, Concurrent, Iteratee}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try


abstract class SocketActor() {

  private var open: Boolean = false
  private val (out, channel) = Concurrent.broadcast[PGPAction]
  private val in = Iteratee.foreach[PGPAction] { msg =>
    onAction(msg)
  } map { _ =>
    doOnClose()
  }

  def refs:(Iteratee[PGPAction, _], Enumerator[PGPAction]) = {
    open = true
    (in, out)
  }

  def send(action: GameAction) {
    if(open) {
      try {
        channel.push(action)
      } catch {
        case scala.util.control.NonFatal(e) =>
          doOnClose()
      }
    }
  }

  def close() {
    if(open) {
      Try {
        channel.eofAndEnd()
      }
      doOnClose()
    }
  }

  def isOpen: Boolean = open

  def onAction(action: PGPAction)
  def onClose()

  private def doOnClose() {
    open = false
    onClose()
  }

}

