
import java.io.{FileInputStream, File}
import java.util

import controllers._
import models.game.{GameDefinitionInfo, GameDefinition, Games}
import org.yaml.snakeyaml.Yaml
import play.api._
import play.api.ApplicationLoader.Context
import router.Routes

class PGPApplicationLoader extends ApplicationLoader {

  def loadGameDefinitions(games: Games): Unit = {
    new File(getClass.getResource("./games").getPath).listFiles().foreach(file => {
      val gameDef = new Yaml().load(new FileInputStream(new File(file, "definition.yml"))).asInstanceOf[util.Map[String, AnyRef]]

      // TODO check for variables
      games.addGameDefinition(new GameDefinition(new GameDefinitionInfo(
        gameDef.get("id").toString,
        gameDef.get("title").toString,
        gameDef.get("color").toString,
        gameDef.get("description").toString
      ), Option(gameDef.get("js_server_file")).map(s => new File(file, s.toString))))
    })
  }

  def load(context: Context) = {
    val games = new Games()
    loadGameDefinitions(games)
    new PGPComponents(context, games).application
  }
}

class PGPComponents(context: Context, games: Games) extends BuiltInComponentsFromContext(context) {

  lazy val app = new IndexController(games)
  lazy val createGame = new CreateGameController(games)
  lazy val database = new DatabaseController()
  lazy val game = new GameController(games)
  lazy val gameDefinition = new GameDefinitionsController(games)
  lazy val joinGame = new JoinGameController(games)
  lazy val leaveGame = new LeaveGameController()
  lazy val socket = new SocketController(games)
  lazy val style = new StyleGuideController()
  lazy val webSocketTest = new WebSocketTestController()

  lazy val assets = new controllers.Assets(httpErrorHandler)



  lazy val router = new Routes(
    httpErrorHandler,
    app,
    game,
    style,
    socket,
    gameDefinition,
    createGame,
    joinGame,
    leaveGame,
    webSocketTest,
    database,
    assets
  )
}
