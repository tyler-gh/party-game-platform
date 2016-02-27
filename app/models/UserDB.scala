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

case class UserDB(userID: Int,
                  userName: String,
                  gameID: Int)

object UserDB {
    implicit val UserDBWrites: Writes[UserDB] = (
      (JsPath \ "userID").write[Int]    and
        (JsPath \ "userName").write[String] and
        (JsPath \ "gameID").write[Int]
      ) (unlift(UserDB.unapply))

  val row = {
      get[Int]("userID") ~
      get[String]("userName") ~
      get[Int]("gameID")map {
      case userID ~ userName ~ gameID =>
        UserDB(userID, userName, gameID)
    }
  }

  def convertFromJson(jsonUsers: JsValue): Seq[UserDB] = {
    Json.parse(jsonUsers.toString()).asOpt[Seq[UserDB]].get
  }

  def convertToJson(users: Seq[UserDB]): JsValue = {
    Json.toJson(users)
  }

  def getUsers(userID : Int = -1, gameID : Int = -1): JsValue = {
    var whereClause = ""
    whereClause = UtilsDB.buildWhereClause(whereClause,"userID",userID)
    whereClause = UtilsDB.buildWhereClause(whereClause,"gameID",gameID)

    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      val result = SQL(s"select * from users $whereClause").as(UserDB.row *)
      convertToJson(result)
    }
  }

  def addUser(userID : Int, userName : String, gameID: Int): Boolean = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      SQL(s"insert into users(userID, userName, gameID) values ('$userID', '$userName' , '$gameID')")
        .executeUpdate()
    } >= 1
  }

  def resetTable(): Boolean = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      SQL("DELETE FROM users").executeUpdate()
    } >= 1
  }

  def createTable(): Boolean  = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      SQL(
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
  }

  def dropTable(): Boolean  = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      SQL("DROP TABLE users CASCADE;").execute()
    }
  }
}
