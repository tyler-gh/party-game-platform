package controllers

import controllers.traits.{WsConnect, CookieAuth}
import models.game.PGPAction
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.mvc._

import scala.concurrent.Future


class SocketController extends CookieAuth[Future[Either[Result, (Iteratee[PGPAction, _], Enumerator[PGPAction], () => Unit)]]] with WsConnect[PGPAction]{


  def socket = tryAccept { implicit request =>
    auth({ (game, client) =>
      Future.successful({
        val connection = client.connection()
        Right((connection._1, connection._2, () => {
          game.onNewClientConnection(client)
        }))
      })
    }, { () =>
      Future.successful(Left(Results.BadRequest))
    })
  }

}
