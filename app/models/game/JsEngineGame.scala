package models.game

import java.io.FileReader
import java.util.function
import javax.script.{ScriptContext, Invocable}

import models.Client
import play.api.libs.json.Json

import util.FuncTransform._
import util.PGPLog._


class JsEngineGame(id: String, name: String, gameDef: GameDefinition) extends Game(id, name, gameDef) {

  private var actionHandler: Option[String] = None
  private var newClientConnectionHandler: Option[String] = None
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
      engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE)
      engine.eval(reader, bindings)
    } finally {
      reader.close()
    }
  })

  def getCreateAction: function.Function[String, String] = {
    (actionStr: String) => {
      val action = Json.parse(actionStr).as[PGPAction]
      Json.toJson(createGameAction(getClient(0).get.clientInfo, action))(GameAction.gameActionWrites).toString()
    }
  }

  def getBroadcastAction: function.Consumer[String] = {
    (action: String) => {
      Json.parse(action).asOpt[GameAction].foreach(actionObj => {
        forEachClient(client => client.sendAction(actionObj))
      })
    }
  }

  def getSendAction: function.BiConsumer[String, String] = {
    (clientsData: String, actionData: String) => {
      (Json.parse(clientsData).asOpt[Seq[Long]], Json.parse(actionData).asOpt[GameAction]) match {
        case (Some(clientTargets), Some(action)) =>
          forEachClient(client => if (clientTargets.contains(client.clientInfo.id)) client.sendAction(action))
        case _ =>
          clientsData.printErrLn()
          actionData.printErrLn()
      }
    }
  }

  def getSetActionHandler: function.Consumer[String] = {
    (handler: String) => {
      actionHandler = Some(handler)
    }
  }

  override def onGameAction(action: GameAction): Unit = {
    actionHandler.foreach(handler => {
      engine.synchronized {
        engine.asInstanceOf[Invocable].invokeFunction(handler, Json.stringify(Json.toJson(action)(GameAction.gameActionWrites)))
      }
    })
  }

  def getSetNewClientConnectionHandler: function.Consumer[String] = {
    (handler: String) => {
      newClientConnectionHandler = Some(handler)
    }
  }

  override def onNewClient(client: Client): Unit = {
    newClientConnectionHandler.foreach(handler => {
      engine.synchronized {
        engine.asInstanceOf[Invocable].invokeFunction(handler, Json.stringify(Json.toJson(client.clientInfo)))
      }
    })
  }
}
