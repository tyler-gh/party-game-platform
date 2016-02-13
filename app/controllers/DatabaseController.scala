package controllers

import models.GameDB
import models.UserDB
import models.ActionDB
import play.api.mvc._
import utils.DB.UtilsDB

//game state: creating
//game state: waiting-room
//game state: playing
//game state: finished

class DatabaseController extends Controller {
  def insertGame(gameID: Int, joinCode : String, gameStatus : String) = Action {
    val success = GameDB.insertRow(gameID, joinCode, gameStatus)
    Ok(views.html.database_result("insert into games table", success.toString(), ""))
  }
  def insertUser(userID : Int, userName : String, gameID: Int) = Action {
    val success = UserDB.insertRow(userID, userName, gameID)
    Ok(views.html.database_result("insert into users table", success.toString(), ""))
  }
  def insertAction(actionNumber : Int, actionType : String, actionData : String, userID : Int, gameID : Int) = Action {
    val success = ActionDB.insertRow(actionNumber, actionType, actionData, userID, gameID)


    Ok(views.html.database_result("insert into actions table", success.toString(), ""))
  }

  def getGames(gameID : Int = -1, joinCode : String = "") = Action {
    val games = GameDB.getRows(gameID,joinCode)
    Ok(games).as("application/json")
  }
  def getUsers(userID : Int = -1, gameID : Int = -1) = Action {
    val games = UserDB.getRows(userID,gameID)
    Ok(games).as("application/json")
  }
  def getActions(startingActionNumber : Int = 0, userID : Int = -1, gameID : Int = -1) = Action {
    val games = ActionDB.getRows(startingActionNumber,userID,gameID)
    Ok(games).as("application/json")
  }

  def deleteGameData(gameID : Int) = Action {
    val success = GameDB.deleteGameData(gameID)
    Ok(views.html.database_result(s"delete game data for $gameID", success.toString(), ""))
  }

  def resetEntireDatabase = Action {
    //by clearing the games table, the rest of the database should be clear
    val success = GameDB.resetTable()
    Ok(views.html.database_result("reset entire Database", success.toString(), ""))
  }
  def createDatabaseTables = Action {
    val success1 = GameDB.createTable()
    val success2 = UserDB.createTable()
    val success3 = ActionDB.createTable()
    
    var success = false
    if(success1 && success2 && success3){
      success = true
    }
    Ok(views.html.database_result("Create Database Tables ", success.toString(), ""))
  }
  def dropDatabaseTables = Action {
    val success3 = ActionDB.dropTable()
    val success2 = UserDB.dropTable()
    val success1 = GameDB.dropTable()

    var success = false
    if(success1 && success2 && success3){
      success = true
    }
    Ok(views.html.database_result("Drop Database Tables", success.toString(), ""))
  }


}
