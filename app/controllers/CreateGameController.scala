package controllers

import models.{ClientCookie, Games}
import play.api.libs.json.Json
import play.api.mvc._

import scala.util.Random

class CreateGameController extends Controller {
  var gameId: Long = 1024

  def create(name: String) = Action {

    val game = Games.createGame(gameId.toHexString, name).get
    val client = game.addClient("root")

    gameId += Random.nextInt(128)

    Ok(Json.obj("gameId" -> game.id, "name" -> client.clientInfo.name, "id" -> client.clientInfo.id)).withCookies(
      ClientCookie.NAME.createCookie(client.clientInfo.name),
      ClientCookie.ID.createCookie(client.clientInfo.id),
      ClientCookie.GAME.createCookie(game.id)
    )
  }
}
