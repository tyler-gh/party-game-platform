package models.game

import java.util
import java.util.Collections

import models.{Client, ClientInfo}

import scala.collection.JavaConverters._

abstract class Game(val id: String, val name: String, val gameDef: GameDefinition) {

  private val clients = Collections.synchronizedList(new util.ArrayList[Option[Client]])
  private val actions = Collections.synchronizedList(new util.ArrayList[GameAction])

  def addClient(clientName: String, color: String): Client = {
    // TODO: having a space in the name caused a problem but I don't remember where
    val client = new Client(this, new ClientInfo(clients.size, clientName, color))
    //todo add users joined -> to userdb
    //todo gameDB isn't being added to yet, so the users cannot be
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

  def performAction(action: GameAction) =
  {
    //todo add action joined -> to actiondb
    //todo gameDB isn't being added to yet, so the users and actions cannot be
     handleAction(action)
  }

  def handleAction(action: GameAction)

  /// TODO remove games from games list when they have no clients
  def endGame(): Unit = {
    forEachClient(client => client.close())
    clients.clear()
  }

  def allClients: List[ClientInfo] = {
    // IDK if we should assume that we shouldn't include people that have left
    // TODO have a client left and a client rejoined action?
    clients.asScala.flatten.map(_.clientInfo).toList
  }

  protected def forEachClient(func: (Client) => Unit): Unit = {
    val it = clients.listIterator()
    while(it.hasNext) {
      it.next().foreach(func)
    }
  }

  protected def findClient(func: (Client) => Boolean): Option[Client] = {
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
