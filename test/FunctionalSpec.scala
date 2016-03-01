import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.ws.WSResponse
import play.api.mvc

import play.api.test._
import play.api.test.Helpers._

import play.api.mvc._
import play.api.test._
import scala.concurrent.duration.Duration
import scala.concurrent._
import play.api.libs.concurrent.Execution.{defaultContext => ec}

import scala.concurrent.Future

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
@RunWith(classOf[JUnitRunner])
class FunctionalSpec extends Specification {

  //TEST ID 1
//  "Given correct URL in browser" should {
//    "Go to the home page" in new WithBrowser(port = 9000) {
//      val home = FunctionalTests.testID_1()
//
//      status(home) must equalTo(OK)
//      contentType(home) must beSome.which(_ == "text/html")
//      contentAsString(home) must contain("PGP")
//    }
//  }
    //TEST ID 1
//    "Given correct URL in browser" should {
//      "Go to the home page" in new WithServer(port = 9000) {
//        val response = Await.result(WsTestClient.wsUrl("/").get(), Duration.Inf)
//        response.status must_== 200
//        //response.
//        response.body must contain("moo57h")
////        val home = browser.goTo("/")
////
////        browser.wait(1000)
////        browser.$("#pg-app").get(0) must equalTo("moo")
//
//      }
//    }
  "Given correct URL in browser" should {
    "Go to the home page" in new WithBrowser(port = 9000) {
      var home = browser.goTo("/")

       //home = browser.goTo("/")
        //var home1 = route(FakeRequest(GET, "/assets/js/index.js")).get
        //var home2 = route(FakeRequest(GET, "/")).get
        //var home3 = browser.goTo("/assets/js/index.js")
        //val moo = route(FakeRequest(GET, "/")).get

        //eventually { contentAsString(browser) must contain("moo")}


        //response.status must_== 200
        //response.
        ///response.body must contain("moo57h")
        //        val home = browser.goTo("/")
        //
        //        browser.wait(1000)
        //browser.$("#pg-app").get(0) must equalTo("moo")
      //browser.pageSource() must contain("moo")

      browser.pageSource() must contain("moo")
    }
  }


}

object FunctionalTests {

  def testID_1(): Future[mvc.Result]  = {

    route(FakeRequest(GET, "/")).get
  }
  def testID_2(): Future[mvc.Result]  = {
    testID_1()
  }
}
