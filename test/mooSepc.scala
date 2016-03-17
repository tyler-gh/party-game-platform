
import java.io.File

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.scalatestplus.play._
import play.api.{ApplicationLoader, Mode, Environment}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{WithApplicationLoader, WithApplication, WithBrowser}
import play.utils.Threads
import play.api.ConfigLoader
import play.api.PlayConfig
import com.google.inject.util
/**
  * Created by Luke on 3/2/2016.
  */

import play.api.db.{Database, Databases}


class mooSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with ChromeFactory  {
  System.setProperty("webdriver.chrome.driver", "c:/chromedriver_win32/chromedriver.exe");
  System.setProperty("webdriver.chrome.logfile", "c:/chromedriver_win32/logFile.log");
  System.setProperty("testserver.port", "9000");



 // val path= app.path //File = buildLink.projectPath
 // val environment = Environment(path, app.classloader, Mode.Test)
 // val context = ApplicationLoader.createContext(environment)
 // val loader = ApplicationLoader(context)
 // val app2 = loader.load(context)
 // val newApplication = Threads.withContextClassLoader(projectClassloader) {
   // val context = loaders.PGPApplicationLoader.createContext(environment, dirAndDevSettings, Some(sourceMapper), webCommands)
 //   val loader = ApplicationLoader(context)
  //  loader.load(context)
  //}

////(new Environment(new File("PGPApplicationLoader"), classLoader, Mode.Test))
   // var application = new GuiceApplicationBuilder(context)
    //.in(Environment(path, app.classloader, Mode.Test))
   // .build();

  /*Databases.withDatabase(
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql://localhost:5432/postgres",
    config = Map(
      "username" -> "postgres",
      "password" -> " "
    )
  ) { database =>
    val connection = database.getConnection()*/


  "Given correct URL in browser" must {

    "Go to the home page" in new WithDepsApplicationLoader {
/*      val database = Databases.inMemory(
        name = "mydatabase",
        urlOptions = Map(
          "MODE" -> "PostgreSQL"
        ),
        config = Map(
          "logStatements" -> true
        )
      )*/

     // app.additionalConfiguration = Map(
      //  "play.application.loader" -> "scaldi.play.ScaldiApplicationLoader"))

      go to (s"http://localhost:9000")
      //pageTitle mustBe "PGP"
      eventually {pageTitle mustBe "mooo"}
      //var home = browser.goTo("/")

      //database.shutdown()

      //var wait1 = new WebDriverWait(webDriver, 50);
      //wait1.until(ExpectedConditions.titleContains("moo"));
     }
    }
    // ...

  //}//end database
}
