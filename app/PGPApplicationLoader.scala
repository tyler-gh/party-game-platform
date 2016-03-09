
import java.io.{FileInputStream, File}
import java.util

import _root_.util.GameAssetsCompiler
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

  val games = new Games().loadDefinitions()
  val compiler = new GameAssetsCompiler(games.getGameDefinitions)

  def load(context: Context) = {

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


