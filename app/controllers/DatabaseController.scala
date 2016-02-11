package controllers

import models.GameDB
import models.UserDB
import models.ActionDB
import play.api.db.DB
import anorm._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
//server can
// reset all games, create game, join game, load all games

//GameList table:
//  Game ID, State of Game, date_created
//UserList table:
  //Game ID, Player State, Player ID, Player Name

//game state: creating
//game state: waiting-room
//game state: playing
//game state: finished
import scala.util.Random

/**
  *
  */
class DatabaseController extends Controller {

  var gameId: Long = 1024

  //tests:
  //insert sample data
  //query if sample data exists
  //detele sample data
  //query if sample data exists

  //insert sample data across whole data base
  //check for consistancy
  //try get actions to x

  //insert action
  //insert game
  //insert user

  //get actions to x
  //get action x

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

    var success1 = GameDB.resetTable()
    var success2 = UserDB.resetTable()
    var success3 = ActionDB.resetTable()

    var success = false
    if(success1 && success2 && success3){
      success = true
    }
    Ok(views.html.database_result("reset entire Database", success.toString(), ""))
  }
  def createDatabaseTables = Action {

    var success1 = GameDB.createTable()
    var success2 = UserDB.createTable()
    var success3 = ActionDB.createTable()

    var success = false
    if(success1 && success2 && success3){
      success = true
    }
    Ok(views.html.database_result("Create Database Tables ", success.toString(), ""))
  }
  def dropDatabaseTables = Action {


    var success3 = ActionDB.dropTable()
    var success2 = UserDB.dropTable()
    var success1 = GameDB.dropTable()

    var success = false
    if(success1 && success2 && success3){
      success = true
    }

    Ok(views.html.database_result("Drop Database Tables", success.toString(), ""))
  }


}
