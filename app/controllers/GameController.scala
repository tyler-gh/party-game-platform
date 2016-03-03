package controllers

import controllers.traits.{FlatAuthError, CookieAuth}
import models.game.Games
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._


class GameController(games: Games) extends Controller with CookieAuth[Result] {

  case class GameExistsParams(gameId: String, gameInstanceId: String)

  implicit val gameExistsReads: Reads[GameExistsParams] = (
    (JsPath \ "game_id").read[String] and
      (JsPath \ "game_instance_id").read[String]
    ) (GameExistsParams.apply _)

  def error(e: JsValue): Result = {
    BadRequest(Json.obj(("error", e)))
  }

  def gameExists(gameIdOpt: Option[String], gameInstanceIdOpt: Option[String]) = Action { implicit request =>

    def checkGame(gameId: String, gameInstanceId: String): Result = {
      games.getGame(gameId, gameInstanceId).fold(BadRequest(Json.obj(("error", JsString("Game does not exist"))))) { game =>
        Ok
      }
    }

    (gameIdOpt, gameInstanceIdOpt) match {
      case (Some(gameId), Some(gameInstanceId)) =>
        checkGame(gameId, gameInstanceId)
      case _ =>
        request.body.asJson.map(_.validate[GameExistsParams]).fold[Result](error(JsString("Missing required parameters"))) {
          case success: JsSuccess[GameExistsParams] =>
            val params = success.get
            checkGame(params.gameId, params.gameInstanceId)
          case e: JsError =>
            error(JsError.toJson(e))
        }
    }
  }

  def get = Action { implicit request =>
    auth(games, (game, client) => {
      val gameInfo = game.gameDef.info
      Ok(views.html.game(gameInfo.id, gameInfo.title, gameInfo.description, game.id, None, game.gameDef.jsClientFiles.fold(false)(_ => true)))
    })(request, new FlatAuthError[Result](Redirect(routes.IndexController.index)))
  }


  def getGameJS = Action { implicit request =>
    auth(games, (game, client) => {
      client.ifMainElse(game.gameDef.jsMainClientFiles)(game.gameDef.jsClientFiles).map(files => {
        Ok(files.map(file => {
          val source = scala.io.Source.fromFile(file.getAbsolutePath.replace(".jsx", ".js"))
          try source.getLines mkString "\n" finally source.close()
        }).reduceLeft(_ + _))
      }).getOrElse(BadRequest(Json.obj(("error", JsString("Game has no client js")))))
    })
  }


}
