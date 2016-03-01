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

//      "Games Database after a reset" should {
//        "be empty " in new WithApplication {
//          UtilsDB.setActiveDatabaseAsTest(true);
//          GameDB.resetTable()
//          val gamesJson = GameDB.getGames()
//          val games = GameDB.convertFromJson(GameDB.getGames())
//          val expectedJson = Json.parse("[]")
//
//          gamesJson must equalTo(expectedJson)
//          games.size must equalTo(0)
//
//        }
//      }
//
//      "Games Database after an insert" should {
//        "be have the correct data " in new WithApplication {
//          UtilsDB.setActiveDatabaseAsTest(true);
//          GameDB.resetTable()
//          GameDB.addGame(1, "asdf")
//          GameDB.addGame(2, "fdsa")
//          val games = GameDB.convertFromJson(GameDB.getGames())
//
//          games.size must equalTo(2)
//          games(0).joinCode must equalTo("asdf")
//          games(1).joinCode must equalTo("fdsa")
//
//        }
//      }

}
