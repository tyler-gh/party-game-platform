package models

import anorm._
import SqlParser._
import play.api.db.DB
import play.api.libs.functional.syntax._
import play.api.libs.json.Json
import play.api.libs.json._
import play.api.Play.current
import utils.DB.UtilsDB
import scala.language.postfixOps

/**
  * Created by Luke on 1/23/2016.
  */

case class ActionDB(actionNumber: Int,
                    actionType: String,
                    actionData: String,
                    userID: Int,
                    gameID:Int)

object ActionDB {

  implicit val actionDBReads: Reads[ActionDB] = (
    (JsPath \ "actionNumber").read[Int]    and
      (JsPath \ "actionType").read[String] and
      (JsPath \ "actionData").read[String] and
      (JsPath \ "userID").read[Int]        and
      (JsPath \ "gameID").read[Int]
    ) (ActionDB.apply _)

  implicit val actionDBWrites: Writes[ActionDB] = (
    (JsPath \ "actionNumber").write[Int]    and
      (JsPath \ "actionType").write[String] and
      (JsPath \ "actionData").write[String] and
      (JsPath \ "userID").write[Int]        and
      (JsPath \ "gameID").write[Int]
    ) (unlift(ActionDB.unapply))

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
  def convertFromJson(jsonActions: JsValue): Seq[ActionDB] = {
    Json.parse(jsonActions.toString()).asOpt[Seq[ActionDB]].get
  }

  def convertToJson(actions: Seq[ActionDB]): JsValue = {
    Json.toJson(actions)
  }

  def getActions(startingActionNumber : Int = 0, userID : Int = -1, gameID : Int = -1): JsValue = {
    var whereClause = ""
    whereClause = s"WHERE actionNumber >= $startingActionNumber"
    whereClause = UtilsDB.buildWhereClause(whereClause,"userID",userID)
    whereClause = UtilsDB.buildWhereClause(whereClause,"gameID",gameID)

    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      val result = SQL(s"select * from actions $whereClause").as(ActionDB.row *)
      convertToJson(result)
    }
  }

  def addAction(actionNumber : Int, actionType : String, actionData : String, userID : Int, gameID : Int): Boolean = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      SQL(
        s"insert into actions(actionNumber, actionType, actionData, userID, gameID) " +
        s"values ('$actionNumber', '$actionType', '$actionData','$userID','$gameID')")
        .executeUpdate()
    } >= 1
  }

  def resetTable(): Boolean = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      SQL("DELETE FROM actions").executeUpdate()
    } >= 1
  }

  def createTable(): Boolean  = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      SQL(
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
  }

  def dropTable(): Boolean  = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      SQL("DROP TABLE actions CASCADE;").execute()
    }
  }

}
