package models

import models.game.{PGPAction, GameAction, Game}
import play.api.libs.iteratee.{Enumerator, Iteratee}

class Client(game: Game, val clientInfo: ClientInfo) {
  private val socket = new ClientSocket(this, game)

  def sendAction(action: GameAction): Unit = {
    socket.send(action)
  }

  def close(): Unit = {
    socket.close()
  }

  def connection(): (Iteratee[PGPAction, _], Enumerator[PGPAction]) = {
    socket.refs
  }

  def isSocketOpen = socket.isOpen

  override def equals(that: Any): Boolean = that match {
    case that: Client => this.hashCode == that.hashCode
    case _ => false
  }

  override def hashCode: Int = {
    clientInfo.id.hashCode()
  }
}


class ClientSocket(client: Client, game: Game) extends SocketActor {

  override def onAction(action: PGPAction) {
    game.performAction(game.createGameAction(client.clientInfo, action))
  }

  override def onClose() {
    game.clientClosed(client)
  }
}