
import java.io.{FileInputStream, File}
import java.util

import _root_.util.GameJsxCompiler
import controllers._
import models.game.{GameDefinitionInfo, GameDefinition, Games}
import org.yaml.snakeyaml.Yaml
import play.api._
import play.api.ApplicationLoader.Context
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._
import router.Routes

import scala.concurrent.Future

class PGPApplicationLoader extends ApplicationLoader {

  def loadGameDefinitions(games: Games): Unit = {
    new File("games").listFiles().filter(_.isDirectory).foreach(folder => {
      val gameDef = new Yaml().load(new FileInputStream(new File(folder, "definition.yml"))).asInstanceOf[util.Map[String, AnyRef]]

      games.addGameDefinition(new GameDefinition(new GameDefinitionInfo(
        gameDef.get("id").asInstanceOf[String],
        gameDef.get("title").asInstanceOf[String],
        gameDef.get("color").asInstanceOf[String],
        gameDef.get("description").asInstanceOf[String]
      ), Option(gameDef.get("js_server_file")).map(file => new File(folder, file.toString)),
        Option(gameDef.get("js_client_files")).map(files => files.asInstanceOf[java.util.ArrayList[String]].asScala.map(new File(folder, _))),
        Option(gameDef.get("js_main_client_files")).map(files => files.asInstanceOf[java.util.ArrayList[String]].asScala.map(new File(folder, _)))
      ))
    })
  }

  def load(context: Context) = {
    val games = new Games()
    loadGameDefinitions(games)
    val compiler = new GameJsxCompiler()

    Future {
      compiler()
    }

    val components = new PGPComponents(context, games)
    components.applicationLifecycle.addStopHook(() => Future {
      compiler.shutdown()
    })
    components.application
  }

}

class PGPComponents(context: Context, games: Games) extends BuiltInComponentsFromContext(context) {

  lazy val app = new IndexController(games)
  lazy val createGame = new CreateGameController(games)
  lazy val database = new DatabaseController()
  lazy val game = new GameController(games)
  lazy val gameDefinition = new GameDefinitionsController(games)
  lazy val joinGame = new JoinGameController(games)
  lazy val leaveGame = new LeaveGameController(games)
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


