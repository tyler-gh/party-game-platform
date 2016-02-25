package controllers

import models.game.{PGPAction, Games}
import models.ClientCookie
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.mvc.WebSocket.FrameFormatter
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class SocketController {

  def tryAccept[A](f: RequestHeader => Future[Either[Result, (Iteratee[A, _], Enumerator[A], () => Unit)]])(implicit frameFormatter: FrameFormatter[A]): WebSocket[A, A] = {
    WebSocket[A, A](f.andThen(_.map { resultOrSocket =>
      resultOrSocket.right.map {
        case (readIn, writeOut, after) => (e:Enumerator[A], i:Iteratee[A,Unit]) => { writeOut |>> i; e |>> readIn; after() }
      }
    }))
  }


  def socket = tryAccept[PGPAction] { request =>
    val cookies = request.cookies

    val nameCookieOpt = ClientCookie.USER_NAME.getCookie(cookies)
    val idCookieOpt = ClientCookie.USER_ID.getCookie(cookies)
    val gameDefIdCookieOpt = ClientCookie.GAME_INSTANCE_ID.getCookie(cookies)
    val gameIdCookieOpt = ClientCookie.GAME_ID.getCookie(cookies)

    Future.successful((nameCookieOpt, idCookieOpt, gameDefIdCookieOpt, gameIdCookieOpt) match {
      case (Some(nameCookie), Some(idCookie), Some(gameDefIdCookie), Some(gameIdCookie)) =>
        Games.getGame(gameIdCookie.value, gameDefIdCookie.value).fold[Either[Result, (Iteratee[PGPAction, _], Enumerator[PGPAction], () => Unit)]](Left(Results.BadRequest))(game => {
          game.getClient(idCookie.value).fold[Either[Result, (Iteratee[PGPAction, _], Enumerator[PGPAction], () => Unit)]](Left(Results.BadRequest))(client => {
            val connection = client.connection()
            Right((connection._1, connection._2, () => {
              game.onNewClientConnection(client)
            }))
          })
        })
      case _ => Left(Results.BadRequest)
    })
  }

}
