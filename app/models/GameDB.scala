package models

import java.util
import java.util.Collections

import anorm._
import SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import play.api.libs.json.Json
import play.api.libs.json._
import play.api.Play.current
/**
  * Created by Luke on 1/23/2016.
  */
//http://www.jamesward.com/2012/02/21/play-framework-2-with-scala-anorm-json-coffeescript-jquery-heroku
//look here to start fixing my garbage
case class GameDB(gameID: Int,
                  joinCode: String,
                  gameState: String,
                  creationTime : DateTime)

//gameID        integer,
//joinCode      text NOT NULL,
//gameState     text NOT NULL,
//creationTime  date DEFAULT current_timestamp,
//PRIMARY KEY(gameID)

object GameDB {

  implicit object GameDBFormat extends Format[GameDB] {
    // convert from GameDB object to JSON (serializing to JSON)
    def writes(gameDB: GameDB): JsValue = {
      val gameDBSeq = Seq(
        "gameID" -> JsNumber(gameDB.gameID),
        "joinCode" -> JsString(gameDB.joinCode),
        "gameState" -> JsString(gameDB.gameState),
        "creationTime" -> JsString(gameDB.creationTime.toString)
      )
      JsObject(gameDBSeq)
   }

    // convert from JSON string to a GameDB object (de-serializing from JSON)
    // (i don't need this method; just here to satisfy the api)
    def reads(json: JsValue): JsResult[GameDB] = {
      JsSuccess(GameDB(-1, "", "", DateTime.now()))
    }

  }

  val row = {
      get[Int]("gameID") ~
      get[String]("joinCode") ~
      get[String]("gameState") ~
      get[DateTime]("creationTime")map {
        case gameID ~ joinCode ~ gameState~ creationTime =>
          GameDB(gameID, joinCode, gameState, creationTime)
      }
  }

  def convertToJson(games: Seq[GameDB]): JsValue = {
    Json.toJson(games)
  }

  def getRows(): JsValue = {
    DB.withConnection { implicit connection =>
      var result = SQL("select * from games").as(GameDB.row *)
      convertToJson(result)
    }
  }

  def insertRow(gameID : Int, joinCode : String, gameState: String = "creating"): Boolean = {
    var success = false
    var result : Int = -1
    DB.withConnection { implicit connection =>
     result = SQL(s"insert into games(gameID, joinCode, gameState) values ('$gameID', '$joinCode', '$gameState')")
       .executeUpdate()
    }
    if(result == -1){
      //todo throw error, not return
      return false
    }

    if(result == 1) {
       success = true
    }
    return success
  }


  def resetTable(): Boolean = {
    var success = false;
    var result :Int = -1

    DB.withConnection { implicit connection =>
      result = SQL("DELETE FROM games").executeUpdate()
    }
    if(result == -1){
      //todo throw error, not return
      return false
    }

    if(result != 0) {
      success = true
    }
    return success
  }

  def createTable(): Boolean  = {
    var success = false;
    var result: Boolean = false;
    DB.withConnection { implicit connection =>
      result = SQL(
        """
          CREATE TABLE IF NOT EXISTS games (
          gameID        integer,
          joinCode      text NOT NULL,
          gameState     text NOT NULL,
          creationTime   timestamp DEFAULT current_timestamp,
          PRIMARY KEY(gameID)
          );
          """
        ).execute()
      }
    if(result == false) {
      success = true
    }

    return success
  }
  def dropTable(): Boolean  = {
    var success = false;
    var result: Boolean = false
    DB.withConnection { implicit connection =>
      result = SQL("DROP TABLE games CASCADE;").execute()
    }
    if(result == false) {
      success =true
    }
    return success
   }

}
