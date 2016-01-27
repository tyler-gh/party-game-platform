package controllers

import models.Database
import play.api.db.DB
import anorm._
import play.api.libs.json.Json
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


  def resetGamesDatabase = Action {

    var success = Database.resetGamesTable()

    Ok(views.html.database_result("reset Games Database", success.toString(), ""))
  }
  def resetUsersDatabase = Action {

    var success = Database.resetUsersTable()

    Ok(views.html.database_result("reset Games Database", success.toString(), ""))
  }
  def resetActionsDatabase = Action {

    var success = Database.resetActionsTable()

    Ok(views.html.database_result("reset Games Database", success.toString(), ""))
  }
  def resetEntireDatabase = Action {

    var success1 = Database.resetGamesTable()
    var success2 = Database.resetUsersTable()
    var success3 = Database.resetActionsTable()

    var success = true
    if(!success1 || !success2 || ! success3){
      success = false
    }
    Ok(views.html.database_result("reset Games Database", success.toString(), ""))
  }
  def createDatabaseTables = Action {

    var success = Database.createDatabaseTables()
    Ok(views.html.database_result("Create Database Tables ", success.toString(), ""))
  }
  def dropDatabaseTables = Action {

    var success = Database.dropDatabaseTables()
    Ok(views.html.database_result("Drop Database Tables", success.toString(), ""))
  }

}
