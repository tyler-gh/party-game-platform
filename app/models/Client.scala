package models

import play.api.libs.iteratee.{Enumerator, Iteratee}


class Client(game: Game, val clientInfo: ClientInfo) {

  private val socket = new ClientSocket(this, game)

  def sendAction(action: GameAction): Unit = {
    socket.send(action)
  }

  def close(): Unit = {
    socket.close()
  }

  def connection(): (Iteratee[GameAction, _], Enumerator[GameAction]) = {
    socket.refs
  }
}


class ClientSocket(client: Client, game: Game) extends SocketActor {
  override def onAction(action: GameAction) {
    game.performAction(action)
  }

  override def onClose() {
    game.clientClosed(client)
  }
}