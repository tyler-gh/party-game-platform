package models.game


import models.Client

/**
  *
  */
class DefaultSocketGame(id: String, name: String, gameDef: GameDefinition) extends Game(id, name, gameDef) {

  private val actions = scala.collection.mutable.ArrayBuffer.empty[GameAction]

  override def onGameAction(action: GameAction): Unit = {
    actions.synchronized {
      actions += action
    }
    forEachClient(client => client.sendAction(action))
  }

  override def onNewClientConnection(client: Client): Unit = {
    actions.synchronized {
      actions.foreach(action => {
        client.sendAction(action)
      })
    }
  }
}
