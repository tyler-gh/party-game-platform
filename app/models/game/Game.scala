package models.game

import java.util
import java.util.Collections

import models.{Client, ClientInfo}
import play.api.libs.json.JsValue

import scala.collection.JavaConverters._

abstract class Game(val id: String, val name: String, val gameDef: GameDefinition) {

  private val clients = Collections.synchronizedList(new util.ArrayList[Option[Client]])
  protected val actionSet = new util.HashSet[GameAction]()

  def addClient(clientName: String, color: String): Client = {
    // TODO: having a space in the name caused a problem but I don't remember where
    val client = new Client(this, new ClientInfo(clients.size, clientName, color))
    performAction(createGameAction(client.clientInfo, GameAction.NEW_CLIENT, None))
    clients.add(Some(client))
    client
  }

  def getClient(clientId: Int): Option[Client] = {
    if(clientId < clients.size)
      clients.get(clientId)
    else
      None
  }

  def createGameAction(clientInfo: ClientInfo, gameActionType: GameAction.Type, data: Option[JsValue]): GameAction = {
    actionSet.synchronized {
      val action = new GameAction(clientInfo, actionSet.size, gameActionType, data)
      actionSet.add(action)
      action
    }
  }

  def createGameAction(clientInfo: ClientInfo, pGPAction: PGPAction): GameAction = {
    createGameAction(clientInfo, pGPAction.gameActionType, pGPAction.data)
  }

  def restoreClient(clientInfo: ClientInfo): Option[Client] = {
    if(clientInfo.id < clients.size) {
      val client = Some(new Client(this, clientInfo))
      performAction(createGameAction(client.get.clientInfo, GameAction.CLIENT_REJOINED, None))
      clients.set(clientInfo.id, client)
      client
    } else {
      None
    }
  }

  def clientClosed(client: Client): Unit = {
    val index = clients.indexOf(client)
    if(index != -1) {
      clients.set(index, None)
    }
    performAction(createGameAction(client.clientInfo, GameAction.CLIENT_LEFT, None))
  }

  @throws(classOf[IllegalArgumentException])
  final def performAction(action: GameAction) {
    if(!actionSet.contains(action)) {
      throw new IllegalArgumentException("Invalid Action")
    }
    onGameAction(action)
  }

  def onGameAction(action: GameAction)
  def onNewClientConnection(client: Client)

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

  override def equals(that: Any): Boolean = that match {
    case that: Game => this.hashCode == that.hashCode
    case _ => false
  }

  override def hashCode(): Int = {
    id.hashCode
  }

}
