package models

import java.util
import java.util.Collections

import anorm._
import play.api.db.DB

/**
  * Created by Luke on 1/23/2016.
  */



object Database {

  def resetGamesTable(): Boolean = {
    var success = true;
    DB.withConnection { implicit c =>
      var result: Boolean =
        SQL("DELETE FROM games").execute()
      if(result == false) {
        success =false
      }
    }
    return success
  }
  def resetUsersTable(): Boolean = {
    var success = true;
    DB.withConnection { implicit c =>
      var result: Boolean =
        SQL("DELETE FROM users").execute()
      if(result == false) {
        success =false
      }
    }
    return success
  }
  def resetActionsTable(): Boolean = {
    var success = true;
    DB.withConnection { implicit c =>
      var result: Boolean =
        SQL("DELETE FROM actions").execute()
      if(result == false) {
        success =false
      }
    }
    return success
  }
  def createDatabaseTables(): Boolean  = {
    var success = true;
    DB.withConnection { implicit c =>
      var result: Boolean =
        SQL("CREATE TABLE IF NOT EXISTS games (" +
          "gameID        integer     PRIMARY KEY," +
          "gameState     varchar(10) DEFAULT creating," +
          "joinCode      varchar(10) NOT NULL," +
          "dateCreated   date DEFAULT current_timestamp," +
          "CONSTRAINT gameID PRIMARY KEY" +
          ")").execute()
      if(result == false) {
          success =false
        }
    }
    DB.withConnection { implicit c =>
      var result: Boolean =
        SQL("CREATE TABLE IF NOT EXISTS users (" +
          "userID        integer     PRIMARY KEY," +
          "userName      varchar(20) NOT NULL," +
          "gameID        integer REFERENCES games(gameID)," +
          "PRIMARY KEY(userID, gameID)" +
          ")").execute()
      if(result == false) {
        success =false
      }
    }
    DB.withConnection { implicit c =>
      var result: Boolean =
        SQL("CREATE TABLE IF NOT EXISTS actions (" +
          "actionNumber  integer DEFAULT nextval('serial')," +
          "userID        integer REFERENCES users(userID)," +
          "gameID        integer REFERENCES games(gameID)" +
          "PRIMARY KEY(actionNumber, userID, gameID)" +
          ")").execute()
      if(result == false) {
        success =false
      }
    }
    return success
  }
  def dropDatabaseTables(): Boolean  = {
    var success = true;
    DB.withConnection { implicit c =>
      var result: Boolean =
      SQL("DROP TABLE games, users, actions;").execute()
      if(result == false) {
        success =false
      }
    }
    return success
  }
}
