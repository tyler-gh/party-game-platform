import java.util.logging.Level

import com.sun.xml.internal.ws.api.Component
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.logging.{LogType, LoggingPreferences}
import org.openqa.selenium.remote.{DesiredCapabilities, CapabilityType}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.inject.guice.GuiceApplicationBuilder
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

  /*System.setProperty("webdriver.chrome.driver", "c:/chromedriver_win32/chromedriver.exe");
  System.setProperty("webdriver.chrome.logfile", "c:/chromedriver_win32/logFile.log");
  var capabilities = DesiredCapabilities.chrome();
  var loggingprefs = new LoggingPreferences();
  loggingprefs.enable(LogType.BROWSER, Level.ALL);
  capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingprefs);*/

  //TEST ID 1
//  "Given correct URL in browserFunctionalSpec.scala:28" should {
   // "Go to the home page" in new WithBrowser(port = 9000) {
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
////        browser.wrunait(1000)
////        browser.$("#pg-app").get(0) must equalTo("moo")
//
//      }
//    }
  //"Given correct URL in browser" should {

  //  "Go to the home page" in new WithBrowser(webDriver = new ChromeDriver(capabilities), port = 9000) {
   //   val application = new GuiceApplicationBuilder().build

   //   var home = browser.goTo("/")



   //   var wait1 = new WebDriverWait(webDriver, 50);
   //   wait1.until(ExpectedConditions.titleContains("moo"));
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
      //WebDriverWait _wait = new WebDriverWait(_driver, new TimeSpan(0, 1, 0));

     // _wait.Until(d => d.FindElement(By.Id("Id_Your_UIElement"));

      //webDriver.wait(10000)
      //webDriver.wait { driver.title.downcase.start_with? "cheese!" }
     // browser.pageSource() must contain("moo")
     // browser.
   // }
 // }


}

object FunctionalTests {

  def testID_1(): Future[mvc.Result]  = {

    route(FakeRequest(GET, "/")).get
  }
  def testID_2(): Future[mvc.Result]  = {
    testID_1()
  }
}
