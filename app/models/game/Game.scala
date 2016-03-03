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

  def clientJoined(client: Client) {
    performAction(createGameAction(client.clientInfo, GameAction.CLIENT_JOINED, None))
  }

  def restoreClient(clientInfo: ClientInfo): Option[Client] = {
    if(clientInfo.id < clients.size) {
      val client = Some(new Client(this, clientInfo))
      clients.set(clientInfo.id, client)
      client
    } else {
      None
    }
  }

  def clientClosed(client: Client): Unit = {
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
  def onNewClient(client: Client)

  final def onNewClientConnection(client: Client) {
    onNewClient(client)
    clientJoined(client)
  }

  /// TODO remove games from games list when they have no clients
  def endGame(): Unit = {
    // TODO maybe set ended flag
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
