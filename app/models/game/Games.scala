package models.game

import java.io.File
import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._
import scala.util.Random


class Games {

  /// Remove Option[Game]
  private val games = new ConcurrentHashMap[String, ConcurrentHashMap[String, Option[Game]]]()
  /// change game instance ids
  private val gameInstanceIds = new ConcurrentHashMap[String, Long]()
  private val definitions = new ConcurrentHashMap[String, GameDefinition]()
  val style = GameDefinition(new File("games/style"))
  val lobby = GameDefinition(new File("games/lobby"))

  def createGame(gameId: String): Option[Game] = {
    getGameDefinition(gameId).fold[Option[Game]](None)(gameDef =>
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
    // memory leak
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

  def getGameDefinitions: Seq[GameDefinition] = {
    definitions.values().asScala.toSeq
  }

  private val SPECIAL_DIRS = collection.immutable.Set("lobby", "style")

  private def isGameDefinition(f: File): Boolean = {
    f.isDirectory && !SPECIAL_DIRS(f.getName)
  }

  def loadDefinitions(): Games = {
    definitions.synchronized {
      new File("games").listFiles().filter(isGameDefinition).foreach(implicit folder => {
        addGameDefinition(GameDefinition(folder))
      })
    }
    this
  }
}
