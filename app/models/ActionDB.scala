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
case class ActionDB(actionNumber: Int,
                    actionType: String,
                    actionData: String,
                    userID: Int,
                    gameID:Int)

object ActionDB {

//  actionNumber  integer,
//  actionType    text NOT NULL,
//  actionData    text NOT NULL,
//  userID        integer,
//  gameID        integer,
//  PRIMARY KEY(actionNumber, userID, gameID),
//  FOREIGN KEY(userID) REFERENCES users(userID),
//  FOREIGN KEY(gameID) REFERENCES games(gameID)

  implicit object ActionDBFormat extends Format[ActionDB] {
    // convert from ActionDB object to JSON (serializing to JSON)
    def writes(actionDB: ActionDB): JsValue = {
      val actionDBSeq = Seq(
        "actionNumber" -> JsNumber(actionDB.actionNumber),
        "actionType" -> JsString(actionDB.actionType),
        "actionData" -> JsString(actionDB.actionData),
        "userID" -> JsNumber(actionDB.userID),
        "gameID" -> JsNumber(actionDB.gameID)
      )
      JsObject(actionDBSeq)
    }

    // convert from JSON string to a ActionDB object (de-serializing from JSON)
    // (i don't need this method; just here to satisfy the api)
    def reads(json: JsValue): JsResult[ActionDB] = {
      JsSuccess(ActionDB(-1,"","",-1,-1))
    }

  }

  val row = {
      get[Int]("actionNumber") ~
      get[String]("actionType") ~
      get[String]("actionData") ~
      get[Int]("userID") ~
      get[Int]("gameID") map {
      case actionNumber ~ actionType ~ actionData ~ userID ~ gameID =>
        ActionDB(actionNumber,actionType,actionData, userID, gameID)
    }
  }

  def convertToJson(actions: Seq[ActionDB]): JsValue = {
    Json.toJson(actions)
  }

  def getRows(startingActionNumber : Int = 0, userID : Int = -1, gameID : Int = -1): JsValue = {
    var whereClause = ""

    whereClause = s"WHERE actionNumber >= $startingActionNumber"
    whereClause = buildWhereClause(whereClause,"userID",userID)
    whereClause = buildWhereClause(whereClause,"gameID",gameID)

    DB.withConnection { implicit connection =>
      val result = SQL(s"select * from actions $whereClause").as(ActionDB.row *)
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

  def insertRow(actionNumber : Int, actionType : String, actionData : String, userID : Int, gameID : Int): Boolean = {
    var success = false
    var result : Int = -1
    DB.withConnection { implicit connection =>
      result = SQL(
        s"insert into actions(actionNumber, actionType, actionData, userID, gameID) " +
        s"values ('$actionNumber', '$actionType', '$actionData','$userID','$gameID')")
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
    var success = true;
    var result: Int = -1;
    DB.withConnection { implicit connection =>
      result = SQL("DELETE FROM actions").executeUpdate()
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
          CREATE TABLE IF NOT EXISTS actions (
          actionNumber  integer,
          actionType    text NOT NULL,
          actionData    text NOT NULL,
          userID        integer,
          gameID        integer,
          PRIMARY KEY(actionNumber, userID, gameID),
          FOREIGN KEY(userID, gameID) REFERENCES users(userID, gameID) ON DELETE CASCADE
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
      result = SQL("DROP TABLE actions CASCADE;").execute()
    }
    if(result == false) {
      success =true
    }
    return success
  }

}
