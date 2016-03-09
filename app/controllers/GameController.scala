package controllers

import java.io.File

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
      Ok(views.html.game(gameInfo.id, gameInfo.title, gameInfo.description, game.id, game.gameDef.jsClientFiles.isDefined, game.gameDef.jsClientFiles.isDefined))
    })(request, new FlatAuthError[Result](Redirect(routes.IndexController.index)))
  }


  def getGameJS = Action { implicit request =>
    auth(games.refreshDefinitionFiles(), (game, client) => {
      client.ifMainElse(game.gameDef.jsMainClientFiles)(game.gameDef.jsClientFiles).map(files => {
        implicit val appendix = (".jsx", ".js")
        Ok(files.map(gameAssetToString).mkString("\n")).withHeaders(CONTENT_TYPE -> "application/js")
      }).getOrElse(BadRequest(Json.obj(("error", JsString("Game has no client js")))))
    })
  }

  def getGameCss = Action { implicit request =>
    auth(games.refreshDefinitionFiles(), (game, client) => {
      game.gameDef.cssClientFiles.map(files => {
        implicit val appendix = (".scss", ".css")
        Ok(files.map(gameAssetToString).mkString("\n").replaceAll("\"_\"",client.clientInfo.colorCode)).withHeaders(CONTENT_TYPE -> "text/css")
      }).getOrElse(BadRequest(Json.obj(("error", JsString("Game has no client css")))))
    })
  }

  def gameAssetToString(file: File)(implicit rep:(String,String)) : String = {
    val source = scala.io.Source.fromFile(file.getAbsolutePath.replace(rep._1, rep._2))
    try source.getLines mkString "\n" finally source.close()
  }


}
