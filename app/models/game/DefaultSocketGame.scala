package models.game

import java.util
import java.util.Collections

import models.{ClientInfo, Client}

/**
  *
  */
class DefaultSocketGame(id: String, name: String, gameDef: GameDefinition) extends Game(id, name, gameDef) {

  private val actions = Collections.synchronizedList(new util.ArrayList[GameAction])

  override def handleAction(action: GameAction): Unit = {
    actions.add(action)
    forEachClient(client => client.sendAction(action))
  }

}
