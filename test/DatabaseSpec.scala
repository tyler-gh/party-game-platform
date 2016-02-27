import models.GameDB
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.json.{JsNull, JsObject, JsSuccess, Json}

import play.api.test._
import play.api.test.Helpers._
import utils.DB.UtilsDB
/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  * For more information, consult the wiki.
  */
@RunWith(classOf[JUnitRunner])
class DatabaseSpec extends Specification {

  UtilsDB.setActiveDatabaseAsTest(true);

  "databases" should {
    "work " in new WithApplication {

     // "Games Database after a reset" should {
     //   "be empty " in {
     //     GameDB.resetTable()
     //     val games = GameDB.convertFromJson(GameDB.getGames())
     //     val expectedJson = Json.parse("[]")

     //     games must equalTo(expectedJson)
     //   }
     // }

      //"Games Database after an insert" should {
      //  "be have the correct data " in {
          GameDB.resetTable()
          GameDB.addGame(1, "asdf")
          GameDB.addGame(2, "fdsa")
          val games = GameDB.convertFromJson(GameDB.getGames())

          games.size must equalTo(2)
          games(0).joinCode must equalTo("asdf")
          games(1).joinCode must equalTo("fdsa")

        }
      }

     // "Application" should {
     //   "work from within a browser" in new WithBrowser {
     //     browser.goTo("http://localhost:9000)") // + port)
    //      browser.pageSource must contain("PGP")
    //    }
     // }

    //}
 // }



  UtilsDB.setActiveDatabaseAsTest(false);


//    "Can parse recursive object" in {
//      val recursiveJson = """{"foo": {"foo":["bar"]}, "bar": {"foo":["bar"]}}"""
//      val expectedJson = JsObject(List(
//        "foo" -> JsObject(List(
//          "foo" -> JsArray(List[JsValue](JsString("bar")))
//        )),
//        "bar" -> JsObject(List(
//          "foo" -> JsArray(List[JsValue](JsString("bar")))
//        ))
//      ))
//      val resultJson = Json.parse(recursiveJson)
//      resultJson must equalTo(expectedJson)





}
