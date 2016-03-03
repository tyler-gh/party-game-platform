package models

import models.game.{PGPAction, GameAction, Game}
import play.api.libs.iteratee.{Enumerator, Iteratee}

class Client(game: Game, val clientInfo: ClientInfo) {

  private var socketOpt:Option[ClientSocket] = None

  final def ifMainElse[T](ifMain: => T)(f: => T): T =
    if (clientInfo.id == 0) ifMain else f


  def sendAction(action: GameAction): Unit = {
    this.synchronized {
      socketOpt.foreach(socket => socket.send(action))
    }
  }

  def close(): Unit = {
    this.synchronized {
      socketOpt.foreach(socket => socket.close())
    }
  }

  def connection(): (Iteratee[PGPAction, _], Enumerator[PGPAction]) = {
    this.synchronized {
      socketOpt = Some(new ClientSocket(this, game, socketClosed))
      socketOpt.get.refs
    }
  }

  private def socketClosed() {
    socketOpt = None
  }


  override def equals(that: Any): Boolean = that match {
    case that: Client => this.hashCode == that.hashCode
    case _ => false
  }

  override def hashCode: Int = {
    clientInfo.id.hashCode()
  }
}


class ClientSocket(client: Client, game: Game, closed:()=>Unit) extends SocketActor {

  override def onAction(action: PGPAction) {
    game.performAction(game.createGameAction(client.clientInfo, action))
  }

  override def onClose() {
    closed()
    game.clientClosed(client)
  }
}