package models

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import scala.util.Random


object Games {
  private val games = new ConcurrentHashMap[String, ConcurrentHashMap[String, Game]]()
  private val gameInstanceIds = new ConcurrentHashMap[String, Long]()
  private val definitions = new ConcurrentHashMap[String, GameDefinition]()

  def createGame(gameId: String): Option[Game] = {
    getGameDefinition(gameId).fold[Option[Game]](None)( gameDef =>
      Option(games.get(gameId)).fold[Option[Game]](None) { defGames =>
        val instanceId = gameInstanceIds.get(gameId)
        val newGame = new Game(instanceId.toHexString, gameId, gameDef)
        defGames.put(instanceId.toHexString, newGame)
        gameInstanceIds.put(gameId, instanceId + Random.nextInt(1024))
        Some(newGame)
      }
    )
  }

  def getGame(gameId: String, gameInstanceId: String): Option[Game] = {
    Option(games.get(gameId)).fold[Option[Game]](None)(defGames => Option(defGames.get(gameInstanceId)))
  }

  def addGameDefinition(definition: GameDefinition) {
    Option(definitions.get(definition.info.id)).fold[Any] {
      definitions.put(definition.info.id, definition)
      games.put(definition.info.id, new ConcurrentHashMap[String, Game]())
    } { gameDef =>
      throw new IllegalArgumentException("Game definition with id '" + gameDef.info.id + "' already exists")
    }
  }

  def getGameDefinition(id: String): Option[GameDefinition] = {
    Option(definitions.get(id))
  }

  def getGameDefinitionsInfo: Iterable[GameDefinitionInfo] = {
    definitions.values().asScala.map(g => g.info)
  }

}
