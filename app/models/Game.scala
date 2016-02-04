package models

import java.io.FileReader
import java.util
import java.util.function.BiConsumer
import java.util.{function, Collections}
import play.api.libs.json.Json

import scala.collection.JavaConverters._

// TODO: pass in game definition
class Game(val id: String, val name: String, val gameDef: GameDefinition) {

  private val clients = Collections.synchronizedList(new util.ArrayList[Option[Client]])
//  private val actions = Collections.synchronizedList(new util.ArrayList[GameAction])
  private var actionHandler: Option[function.Consumer[String]] = None
  private val engine = GameScriptEngine.getNewEngine
  gameDef.jsServerFile.map(file => new FileReader(file)).foreach(reader => {
    engine.synchronized {
      val bindings = engine.createBindings()
      // TODO error handler
      bindings.put("setActionHandler", getSetActionHandler)
      bindings.put("broadcastAction", getBroadcastAction)
      bindings.put("sendAction", getSendAction)
      engine.eval(reader, bindings)
    }
  })

  def addClient(clientName: String, color: String): Client = {
    // TODO: having a space in the name caused a problem but I don't remember where
    val client = new Client(this, new ClientInfo(clients.size, clientName, color))
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

  def getBroadcastAction: function.Consumer[String] = {
    new function.Consumer[String] {
      override def accept(action: String) {
        // TODO fold with error
        Json.parse(action).asOpt[GameAction].foreach(actionObj => {
          forEachClient(client => client.sendAction(actionObj))
        })
      }
    }
  }

  def getSendAction: function.BiConsumer[String, String] = {
    new BiConsumer[String, String] {
      override def accept(clientsData: String, actionData: String) {
        (Json.parse(clientsData).asOpt[Seq[Long]], Json.parse(actionData).asOpt[GameAction]) match {
          case (Some(clientTargets),Some(action)) =>
            forEachClient(client => if(clientTargets.contains(client.clientInfo.id)) client.sendAction(action))
          case _ => // TODO error
        }
      }
    }
  }

  def getSetActionHandler: function.Consumer[function.Consumer[String]] = {
    new function.Consumer[function.Consumer[String]] {
      override def accept(handler: function.Consumer[String]) {
        actionHandler = Some(handler)
      }
    }
  }

  def performAction(action: GameAction): Unit = {
    actionHandler.foreach(handler => {
      handler.synchronized {
        handler.accept(Json.stringify(Json.toJson(action)))
      }
    })
//    actions.add(action)
//    forEachClient(client => client.sendAction(action))
  }

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
