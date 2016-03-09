package models.game

import java.io.{FileInputStream, File}
import java.util
import java.util.concurrent.ConcurrentHashMap

import org.yaml.snakeyaml.Yaml

import scala.collection.JavaConverters._
import scala.util.Random


class Games {

  private val games = new ConcurrentHashMap[String, ConcurrentHashMap[String, Option[Game]]]()

  private val gameInstanceIds = new ConcurrentHashMap[String, Long]()
  private val definitions = new ConcurrentHashMap[String, GameDefinition]()

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

  def refreshDefinitionFiles(): Games = {
    new File("games").listFiles().filter(_.isDirectory).foreach(implicit folder => {
      implicit val gameValues = loadYaml(new File(folder, "definition.yml"))
      getGameDefinition(getMapValue("id").get).foreach(gameDefinition => {
        gameDefinition.jsClientFiles = getOptionalList("js_client_files")
        gameDefinition.jsMainClientFiles = getOptionalList("js_main_client_files")
      })
    })
    this
  }

  def loadDefinitions(): Games = {
    definitions.synchronized {
      new File("games").listFiles().filter(_.isDirectory).foreach(implicit folder => {
        implicit val gameValues = loadYaml(new File(folder, "definition.yml"))

        addGameDefinition(new GameDefinition(
          folder,
          new GameDefinitionInfo(
            getMapValue("id").get,
            getMapValue("title").get,
            getMapValue("color").get,
            getMapValue("description").get
          ), getMapValue("js_server_file", Some((file: String) => new File(folder, file))),
          getOptionalList("js_client_files"),
          getOptionalList("js_main_client_files"),
          getOptionalList("css_client_files")
        ))
      })
    }
    this
  }

  private def loadYaml(file: File): util.Map[String, AnyRef] = {
    new Yaml().load(new FileInputStream(file)).asInstanceOf[util.Map[String, AnyRef]]
  }

  private def getOptionalList(key: String)(implicit map: util.Map[String, AnyRef], folder: File): Option[Seq[File]] = {
    getMapValue[util.ArrayList[String], Seq[File]](key, Some(_.asScala.map(new File(folder, _))))
  }

  private def getMapValue[T, R](key: String, transform: Option[T => R] = None)(implicit map: util.Map[String, AnyRef]): Option[R] = {
    Option(map.get(key)).map(value => transform.fold(value.asInstanceOf[R])(f => f(value.asInstanceOf[T])))
  }
}
