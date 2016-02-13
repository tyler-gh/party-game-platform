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

  def convertToJson(actions: Seq[ActionDB]): JsValue = {
    Json.toJson(actions)
  }

  def getRows(startingActionNumber : Int = 0, userID : Int = -1, gameID : Int = -1): JsValue = {
    var whereClause = ""
    whereClause = s"WHERE actionNumber >= $startingActionNumber"
    whereClause = UtilsDB.buildWhereClause(whereClause,"userID",userID)
    whereClause = UtilsDB.buildWhereClause(whereClause,"gameID",gameID)

    DB.withConnection { implicit connection =>
      val result = SQL(s"select * from actions $whereClause").as(ActionDB.row *)
      convertToJson(result)
    }
  }

  def insertRow(actionNumber : Int, actionType : String, actionData : String, userID : Int, gameID : Int): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
        s"insert into actions(actionNumber, actionType, actionData, userID, gameID) " +
        s"values ('$actionNumber', '$actionType', '$actionData','$userID','$gameID')")
        .executeUpdate()
    } >= 1
  }

  def resetTable(): Boolean = {
    DB.withConnection { implicit connection =>
      SQL("DELETE FROM actions").executeUpdate()
    } >= 1
  }

  def createTable(): Boolean  = {
    DB.withConnection { implicit connection =>
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
    DB.withConnection { implicit connection =>
      SQL("DROP TABLE actions CASCADE;").execute()
    }
  }

}
