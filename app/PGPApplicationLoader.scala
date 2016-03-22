
import _root_.util.GameAssetsCompiler
import controllers._
import models.game.Games
import play.api._
import play.api.ApplicationLoader.Context
import scala.concurrent.ExecutionContext.Implicits.global
import router.Routes


import scala.concurrent.Future

class PGPApplicationLoader extends ApplicationLoader {

  implicit val games = new Games().loadDefinitions()
  val compiler = new GameAssetsCompiler(games)

  def load(context: Context) = {
    val components = new PGPComponents(context)

    if(Play.isDev(components.application)) {
      compiler.initialCompilation()

      Future {
        compiler()
      }

      components.applicationLifecycle.addStopHook(() => Future {
        compiler.shutdown()
      })
    }

    components.application
  }

}

class PGPComponents(context: Context)(implicit games: Games) extends BuiltInComponentsFromContext(context) {

  lazy val app = new IndexController()
  lazy val createGame = new CreateGameController()
  lazy val database = new DatabaseController()
  lazy val game = new GameController()
  lazy val gameDefinition = new GameDefinitionsController()
  lazy val joinGame = new JoinGameController()
  lazy val leaveGame = new LeaveGameController()
  lazy val socket = new SocketController()
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


