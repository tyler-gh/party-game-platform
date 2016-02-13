package models.game

import java.io.FileReader
import java.util.function.BiConsumer
import java.util.function

import play.api.libs.json.Json

/**
  *
  */
class JsEngineGame(id: String, name: String, gameDef: GameDefinition) extends Game(id, name, gameDef) {

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

  def handleAction(action: GameAction): Unit = {
    actionHandler.foreach(handler => {
      handler.synchronized {
        handler.accept(Json.stringify(Json.toJson(action)))
      }
    })
  }



}
