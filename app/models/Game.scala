package models

import scala.collection.mutable.ArrayBuffer

class Game {

  private val clients = ArrayBuffer.empty[Option[Client]]

  def addClient(name: String): Client = {
    val client = new Client(this, new ClientInfo(clients.size, name))
    clients += Some(client)
    performAction(new GameAction(client.clientInfo, GameAction.NEW_CLIENT, None))
    client
  }

  def clientClosed(client: Client): Unit = {
    clients.transform(clientOpt => if (clientOpt.contains(client)) None else clientOpt)
  }

  def performAction(action: GameAction): Unit = {
    forEachClient(client => client.sendAction(action))
  }

  def endGame(): Unit = {
    forEachClient(client => client.close())
    clients.clear()
  }

  private def forEachClient(func: (Client) => Unit): Unit = {
    clients.foreach(clientOpt => clientOpt.foreach(func))
  }

}
