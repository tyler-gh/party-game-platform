import java.io.File

import models.ClientCookie
import models.game.{GameAction, ClientAction}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.json.{JsString, JsObject}

import play.api.test._
import play.api.test.Helpers._


@RunWith(classOf[JUnitRunner])
class CreateGameSpec extends Specification {

  "Create Game" should {

    "create game upon valid request" in new WithDepsApplication {
      val game = route(FakeRequest(POST, "/create_game").withJsonBody(JsObject(Seq("game_id" -> JsString("pirate"))))).get

      status(game) must equalTo(OK)
      contentType(game) must beSome.which(_ == "application/json")
      cookies(game).get(ClientCookie.ACTIVE_GAME.name).map(_.value) mustEqual Some("true")
      cookies(game).get(ClientCookie.USER_ID.name).map(_.value) mustEqual Some("0")
      cookies(game).get(ClientCookie.GAME_INSTANCE_ID.name).map(_.value.length) mustEqual Some(5)
      cookies(game).get(ClientCookie.GAME_ID.name).map(_.value) mustEqual Some("pirate")
    }

    "non existant game not found" in new WithDepsApplication {
      val game = route(FakeRequest(POST, "/create_game").withJsonBody(JsObject(Seq("game_id" -> JsString("franky sonatora"))))).get

      status(game) must equalTo(NOT_FOUND)
    }

    "create game without request params fails" in new WithDepsApplication {
      val game = route(FakeRequest(POST, "/create_game")).get

      status(game) must equalTo(BAD_REQUEST)
    }
  }
}
