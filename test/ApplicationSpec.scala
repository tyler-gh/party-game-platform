import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._


@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithDepsApplication {
      route(FakeRequest(GET, "/boum")) must beSome.which(status(_) == NOT_FOUND)
    }

    "render the index page" in new WithDepsApplication {
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain("<div id=\"pg-app\"></div>")
    }

    "load lobby js" in new WithDepsApplication {
      val lobbyjs = route(FakeRequest(GET, "/js")).get

      status(lobbyjs) must equalTo(OK)
      contentType(lobbyjs) must beSome.which(_ == "text/javascript")
      contentAsString(lobbyjs) must contain("React.createClass")
    }

    "load game main js" in new WithDepsApplication {
      val gameMainJs = route(FakeRequest(GET, "/game/js/main")).get

      status(gameMainJs) must equalTo(OK)
      contentType(gameMainJs) must beSome.which(_ == "text/javascript")
      contentAsString(gameMainJs) must contain("React.createClass")
    }

    "load lobby css" in new WithDepsApplication {
      val lobbyCss = route(FakeRequest(GET, "/css")).get

      status(lobbyCss) must equalTo(OK)
      contentType(lobbyCss) must beSome.which(_ == "text/css")
    }

    "redirect if logged in" in new WithDepsApplication {
      // TODO: create test for this with mocks
    }


  }
}
