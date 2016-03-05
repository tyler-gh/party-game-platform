package models.game

import java.io.FileReader
import java.util.function
import java.util.function.{BiConsumer, Consumer}

import models.Client
import play.api.libs.json.Json

import util.PGPLog._


class JsEngineGame(id: String, name: String, gameDef: GameDefinition) extends Game(id, name, gameDef) {

  private var actionHandler: Option[function.Consumer[String]] = None
  private var newClientConnectionHandler: Option[function.Consumer[String]] = None
  private val engine = GameScriptEngine.getNewEngine

  gameDef.jsServerFile.map(file => new FileReader(file)).foreach(reader => {
    try {
      val bindings = engine.createBindings()
      // TODO error handler
      bindings.put("setActionHandler", getSetActionHandler)
      bindings.put("setNewClientConnectionHandler", getSetNewClientConnectionHandler)
      bindings.put("broadcastAction", getBroadcastAction)
      bindings.put("sendAction", getSendAction)
      bindings.put("createAction", getCreateAction)
      engine.eval(reader, bindings)
    } finally {
      reader.close()
    }
  })

  def getCreateAction: function.Function[String, String] = {
    new function.Function[String, String] {
      override def apply(actionStr: String): String = {
        val action = Json.parse(actionStr).as[PGPAction]
        Json.toJson(createGameAction(getClient(0).get.clientInfo, action))(GameAction.gameActionWrites).toString()
      }
    }
  }

  def getBroadcastAction: function.Consumer[String] = {
    new Consumer[String] {
      override def accept(action: String): Unit = {
        Json.parse(action).asOpt[GameAction].foreach(actionObj => {
          forEachClient(client => client.sendAction(actionObj))
        })
      }
    }
  }

  def getSendAction: function.BiConsumer[String, String] = {
    new BiConsumer[String, String] {
      override def accept(clientsData: String, actionData: String): Unit = {
        (Json.parse(clientsData).asOpt[Seq[Long]], Json.parse(actionData).asOpt[GameAction]) match {
          case (Some(clientTargets), Some(action)) =>
            forEachClient(client => if (clientTargets.contains(client.clientInfo.id)) client.sendAction(action))
          case _ =>
            clientsData.printErrLn()
            actionData.printErrLn()
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

  override def onGameAction(action: GameAction): Unit = {
    actionHandler.foreach(handler => {
      handler.synchronized {
        handler.accept(Json.stringify(Json.toJson(action)(GameAction.gameActionWrites)))
      }
    })
  }

  def getSetNewClientConnectionHandler: function.Consumer[function.Consumer[String]] = {
    new function.Consumer[function.Consumer[String]] {
      override def accept(handler: function.Consumer[String]) {
        newClientConnectionHandler = Some(handler)
      }
    }
  }

  override def onNewClient(client: Client): Unit = {
    newClientConnectionHandler.foreach(handler => {
      handler.synchronized {
        handler.accept(Json.stringify(Json.toJson(client.clientInfo)))
      }
    })
  }
}
