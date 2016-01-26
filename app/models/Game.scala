package models

import java.util
import java.util.Collections


class Game(val id: String, val name: String) {

  private val clients = Collections.synchronizedList(new util.ArrayList[Option[Client]])
  private val actions = Collections.synchronizedList(new util.ArrayList[GameAction])

  def addClient(clientName: String): Client = {
    // TODO: no spaces in names
    val client = new Client(this, new ClientInfo(clients.size, clientName))
    performAction(new GameAction(client.clientInfo, GameAction.NEW_CLIENT, None))
    clients.add(Some(client))
    client
  }

  def getClient(clientId: Long): Option[Client] = {
    findClient(client => client.clientInfo.id == clientId)
  }

  def clientClosed(client: Client): Unit = {
    val index = clients.indexOf(client)
    if(index != -1) {
      clients.set(index, None)
    }
  }

  def performAction(action: GameAction): Unit = {
    actions.add(action)
    forEachClient(client => client.sendAction(action))
  }

  def endGame(): Unit = {
    forEachClient(client => client.close())
    clients.clear()
  }

  def allClients: List[ClientInfo] = {
    clients.toArray(new Array[Option[Client]](clients.size())).toList.flatten.map(_.clientInfo)
  }

  private def forEachClient(func: (Client) => Unit): Unit = {
    val it = clients.listIterator()
    while(it.hasNext) {
      it.next().foreach(func)
    }
  }

  private def findClient(func: (Client) => Boolean): Option[Client] = {
    val it = clients.listIterator()
    while(it.hasNext) {
      val client = it.next()
      if(client.isDefined) {
        if(func.apply(client.get)) {
          return Some(client.get)
        }
      }
    }
    None
  }

  override def equals(that: Any): Boolean = that match {
    case that: Game => this.hashCode == that.hashCode
    case _ => false
  }

  override def hashCode(): Int = {
    id.hashCode
  }

}
