package models

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._


object Games {
  private val games = new ConcurrentHashMap[String, Game]()
  private val definitions = new ConcurrentHashMap[String, GameDefinition]()

  // todo: hook the game up to a game definition id
  def createGame(id: String, name: String): Option[Game] = {
    if(games.containsKey(id)) {
      None
    } else {
      val newGame = new Game(id, name)
      games.put(id, newGame)
      Some(newGame)
    }
  }

  def getGame(id: String): Option[Game] = {
    Option(games.get(id))
  }

  def addGameDefinition(definition: GameDefinition) {
    if(definitions.containsKey(definition.id)) {
      throw new IllegalArgumentException("Game definition with id '" + definition.id + "' already exists")
    } else {
      definitions.put(definition.id, definition)
    }
  }

  def getGameDefinition(id: String): Option[GameDefinition] = {
    Option(definitions.get(id))
  }

  def getGameDefinitions : Iterable[GameDefinition] = {
    definitions.values().asScala
  }

}
