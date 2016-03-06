package controllers

import controllers.traits.{AuthError, WsConnect, CookieAuth}
import models.game.{Games, PGPAction}
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.mvc._

import scala.concurrent.Future


class SocketController(games: Games) extends CookieAuth[Future[Either[Result, (Iteratee[PGPAction, _], Enumerator[PGPAction], () => Unit)]]] with WsConnect[PGPAction]{

  def socket = tryAccept { implicit request =>
    auth(games, { (game, client) =>
      Future.successful({
        val connection = client.connection()
        Right((connection._1, connection._2, () => {
          game.onNewClientConnection(client)
        }))
      })
    })
  }


  implicit val socketAuthError = new AuthError[Future[Either[Result, (Iteratee[PGPAction, _], Enumerator[PGPAction], () => Unit)]]] {
    def error(e: Result)  = {
      Future.successful(Left(e))
    }
    override def missingCookie() = {
      error(AuthError.mvcResultAuthError.missingCookie())
    }
    override def notAMemberOfGame() = {
      error(AuthError.mvcResultAuthError.notAMemberOfGame())
    }
    override def leftGame() = {
      error(AuthError.mvcResultAuthError.leftGame())
    }
    override def gameDNE() = {
      error(AuthError.mvcResultAuthError.gameDNE())
    }
  }

}
