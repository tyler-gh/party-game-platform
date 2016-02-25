package models

import anorm._
import SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import play.api.libs.functional.syntax._
import play.api.libs.json.Json
import play.api.libs.json._
import play.api.Play.current
import play.api.mvc.WebSocket.FrameFormatter
import utils.DB.UtilsDB
import scala.language.postfixOps

/**
  * Created by Luke on 1/23/2016.
  */

case class GameDB(gameID: Int,
                  joinCode: String,
                  gameState: String,
                  creationTime : DateTime)

object GameDB {

  implicit val GameDBReads: Reads[GameDB] = (
    (JsPath \ "gameID").read[Int]    and
      (JsPath \ "joinCode").read[String] and
      (JsPath \ "gameState").read[String] and
      (JsPath \ "creationTime").read[DateTime]
    ) (GameDB.apply _)

    implicit val GameDBWrites: Writes[GameDB] = (
      (JsPath \ "gameID").write[Int]    and
        (JsPath \ "joinCode").write[String] and
        (JsPath \ "gameState").write[String] and
        (JsPath \ "creationTime").write[DateTime]
      ) (unlift(GameDB.unapply))

  val row = {
      get[Int]("gameID") ~
      get[String]("joinCode") ~
      get[String]("gameState") ~
      get[DateTime]("creationTime")map {
        case gameID ~ joinCode ~ gameState~ creationTime =>
          GameDB(gameID, joinCode, gameState, creationTime)
      }
  }

  def convertFromJson(jsonGames: JsValue): JsValue = {
    Json.parse(jsonGames.toString())
  }

  def convertToJson(games: Seq[GameDB]): JsValue = {
    Json.toJson(games)
  }

  def getGames(gameID : Int = -1, joinCode : String = ""): JsValue = {
    var whereClause = ""
    whereClause = UtilsDB.buildWhereClause(whereClause,"gameID",gameID)
    whereClause = UtilsDB.buildWhereClause(whereClause,"joinCode",joinCode)

    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      val result = SQL(s"select * from games $whereClause").as(GameDB.row *)
      convertToJson(result)
    }
  }

  def addGame(gameID : Int, joinCode : String, gameState: String = "creating"): Boolean = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
     SQL(s"insert into games(gameID, joinCode, gameState) values ('$gameID', '$joinCode', '$gameState')")
       .executeUpdate()
    } >= 1
  }

  def deleteGameData(gameID : Int): Boolean = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
       SQL(
        s"DELETE from games where gameID=$gameID").executeUpdate()
    } >= 1
  }

  def resetTable(): Boolean = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      SQL("DELETE FROM games").executeUpdate()
    } >= 1
  }

  def createTable(): Boolean  = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      SQL(
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
  }

  def dropTable(): Boolean  = {
    DB.withConnection(UtilsDB.getActiveDatabaseName()) { implicit connection =>
      SQL("DROP TABLE games CASCADE;").execute()
    }
   }

}
