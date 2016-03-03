package models.game

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._
import scala.util.Random


class Games {
  private val games = new ConcurrentHashMap[String, ConcurrentHashMap[String, Option[Game]]]()

  private val gameInstanceIds = new ConcurrentHashMap[String, Long]()
  private val definitions = new ConcurrentHashMap[String, GameDefinition]()
  def createGame(gameId: String): Option[Game] = {
    getGameDefinition(gameId).fold[Option[Game]](None)( gameDef =>
      Option(games.get(gameId)).fold[Option[Game]](None) { gameInstances =>
        val instanceId = gameInstanceIds.get(gameId)
        val idHex = instanceId.toHexString
        val newGame = Some(gameDef.jsServerFile.fold[Game](new DefaultSocketGame(idHex, gameId, gameDef))(file => new JsEngineGame(idHex, gameId, gameDef)))
        gameInstances.put(instanceId.toHexString, newGame)
        gameInstanceIds.put(gameId, instanceId + Random.nextInt(1024))
        newGame
      }
    )
  }

  def getGame(gameId: String, gameInstanceId: String): Option[Game] = {
    Option(games.get(gameId)).fold[Option[Game]](None)(gameInstances => Option(gameInstances.get(gameInstanceId)).flatten)
  }

  def closeGame(game: Game) {
    game.endGame()
    Option(games.get(game.gameDef.info.id)).foreach(_.put(game.id, None))
  }

  def addGameDefinition(definition: GameDefinition) {
    Option(definitions.get(definition.info.id)).fold[Any] {
      definitions.put(definition.info.id, definition)
      games.put(definition.info.id, new ConcurrentHashMap[String, Option[Game]]())
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
