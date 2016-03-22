import java.io.File

import models.{Client, ClientInfo}
import models.game.{Game, GameAction, ClientAction}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._


class ClientMock(game: Game, info: ClientInfo) extends Client(game, info) {
  var actions = Seq[GameAction]()

  override def sendAction(action: GameAction):Unit = {
    actions = actions :+ action
  }
}


@RunWith(classOf[JUnitRunner])
class PirateGameTestSpec extends Specification {

  def createClientMock(game: Game, info: ClientInfo): Client = {
    new ClientMock(game, info)
  }

  "Pirate Game" should {

    val loader:PGPApplicationLoader = new PGPApplicationLoader(new File(".").getAbsoluteFile)

    "simple game" in new WithDepsApplication(loader) {

      val game = loader.games.createGame("pirate").get

      val root = game.addClient("root", "black", createClientMock).asInstanceOf[ClientMock]
      val fred = game.addClient("fred", "pink", createClientMock).asInstanceOf[ClientMock]
      val george = game.addClient("george", "orange", createClientMock).asInstanceOf[ClientMock]
      val ginny = game.addClient("ginny", "green", createClientMock).asInstanceOf[ClientMock]

      game.onNewClientConnection(root)
      game.onNewClientConnection(fred)
      game.onNewClientConnection(george)
      game.onNewClientConnection(ginny)

      game.performAction(game.createGameAction(fred.clientInfo, new ClientAction(GameAction.withName("start-game"), None)))

      fred.actions.last.gameActionType.name mustEqual "prompt-turn"
    }

  }
}

