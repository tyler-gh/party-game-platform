package models

import java.sql.Date
import java.util
import java.util.Collections

import anorm._
import SqlParser._
import play.api.db.DB
import play.api.libs.json.Json
import play.api.libs.json._
import play.api.Play.current
/**
  * Created by Luke on 1/23/2016.
  */
//http://www.jamesward.com/2012/02/21/play-framework-2-with-scala-anorm-json-coffeescript-jquery-heroku
//look here to start fixing my garbage
case class UserDB(userID: Int,
                  userName: String,
                  gameID: Int)

object UserDB {

//  userID        integer,
//  userName      text NOT NULL,
//  gameID        integer,
//  PRIMARY KEY(userID, gameID),
//  FOREIGN KEY(gameID) REFERENCES games(gameID)

  implicit object UserDBFormat extends Format[UserDB] {
    // convert from UserDB object to JSON (serializing to JSON)
    def writes(userDB: UserDB): JsValue = {
      val userDBSeq = Seq(
        "userID" -> JsNumber(userDB.userID),
        "userName" -> JsString(userDB.userName),
        "gameID" -> JsNumber(userDB.gameID)
      )
      JsObject(userDBSeq)
    }

    // convert from JSON string to a UserDB object (de-serializing from JSON)
    // (i don't need this method; just here to satisfy the api)
    def reads(json: JsValue): JsResult[UserDB] = {
      JsSuccess(UserDB(-1, "",-1))
    }
  }

  val row = {
      get[Int]("userID") ~
      get[String]("userName") ~
      get[Int]("gameID")map {
      case userID ~ userName ~ gameID =>
        UserDB(userID, userName, gameID)
    }
  }

  def convertToJson(users: Seq[UserDB]): JsValue = {
    Json.toJson(users)
  }

  def getRows(userID : Int = -1, gameID : Int = -1): JsValue = {

    var whereClause = ""

    whereClause = buildWhereClause(whereClause,"userID",userID)
    whereClause = buildWhereClause(whereClause,"gameID",gameID)

    DB.withConnection { implicit connection =>
      val result = SQL(s"select * from users $whereClause").as(UserDB.row *)
      convertToJson(result)
    }
  }

  //TODO refactor this out to common db file?

  def buildWhereClause(whereClause : String, colName : String, value: Int): String = {
    var valueString = ""
    if(value != -1)
    valueString = value.toString()

    return buildWhereClause(whereClause,colName,valueString)
  }
  //TODO refactor this out to common db file?
  def buildWhereClause(whereClause : String, colName : String, value: String): String = {
    var newWhereClause = whereClause
    if(value != ""){
      if(newWhereClause == ""){
        newWhereClause += s" WHERE $colName = $value"
      }
      else
      {
        newWhereClause += s" AND $colName = $value"
      }
    }
    return newWhereClause
  }

  def insertRow(userID : Int, userName : String, gameID: Int): Boolean = {
    var success = false
    var result : Int = -1
    DB.withConnection { implicit connection =>
      result = SQL(s"insert into users(userID, userName, gameID) values ('$userID', '$userName' , '$gameID')")
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
    var result: Int = -1;
    DB.withConnection { implicit connection =>
      result = SQL("DELETE FROM users").executeUpdate()
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
        CREATE TABLE IF NOT EXISTS users (
        userID        int,
        userName      text NOT NULL,
        gameID        integer,
        PRIMARY KEY(userID, gameID),
        FOREIGN KEY(gameID) REFERENCES games(gameID) ON DELETE CASCADE
          )
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
      result = SQL("DROP TABLE users CASCADE;").execute()
    }
    if(result == false) {
      success =true
    }
    return success
  }
}
