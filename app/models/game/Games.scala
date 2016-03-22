package models.game

import java.io.File
import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._
import scala.util.Random

object Games {
  val ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray
  val ID_LENGTH = 5

  def getId(implicit rand: Random): String = {
    new String(Array.fill(ID_LENGTH){ID_CHARS(rand.nextInt(ID_CHARS.length))})
  }

  def getUniqueId(currentIds: java.util.Set[String])(implicit rand: Random): String = {
    var id:String = null
    do {
      id = getId
    } while(currentIds.contains(id))
    id
  }
}

class Games(rootDirectory: File) {
  private implicit val rand = new Random()

  private val games = new ConcurrentHashMap[String, ConcurrentHashMap[String, Game]]()
  private val definitions = new ConcurrentHashMap[String, GameDefinition]()
  val style = GameDefinition(new File(rootDirectory, "games/style"))
  val lobby = GameDefinition(new File(rootDirectory, "games/lobby"))

  def createGame(gameId: String): Option[Game] = {
    getGameDefinition(gameId).fold[Option[Game]](None)(gameDef =>
      Option(games.get(gameId)).fold[Option[Game]](None) { gameInstances =>
        val idHex = Games.getUniqueId(gameInstances.keySet())
        val newGame = gameDef.jsServerFile.fold[Game](new DefaultSocketGame(idHex, gameId, gameDef))(file => new JsEngineGame(idHex, gameId, gameDef))
        gameInstances.put(idHex, newGame)
        Some(newGame)
      }
    )
  }

  def getGame(gameId: String, gameInstanceId: String): Option[Game] = {
    Option(games.get(gameId)).fold[Option[Game]](None)(gameInstances => Option(gameInstances.get(gameInstanceId.toUpperCase)))
  }

  def closeGame(game: Game) {
    game.endGame()
    Option(games.get(game.gameDef.info.id)).foreach(_.remove(game.id))
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

  def getGameDefinitions: Seq[GameDefinition] = {
    definitions.values().asScala.toSeq
  }

  private val SPECIAL_DIRS = collection.immutable.Set("lobby", "style")

  private def isGameDefinition(f: File): Boolean = {
    f.isDirectory && !SPECIAL_DIRS(f.getName)
  }

  def loadDefinitions: Games = {
    definitions.synchronized {
      new File(rootDirectory, "games").listFiles().filter(isGameDefinition).foreach(implicit folder => {
        addGameDefinition(GameDefinition(folder))
      })
    }
    this
  }
}
