package controllers.traits

import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.mvc.WebSocket.FrameFormatter
import play.api.mvc.{WebSocket, Result, RequestHeader}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


trait WsConnect[A] {
  def tryAccept(f: RequestHeader => Future[Either[Result, (Iteratee[A, _], Enumerator[A], () => Unit)]])(implicit frameFormatter: FrameFormatter[A]): WebSocket[A, A] = {
    WebSocket[A, A](f.andThen(_.map { resultOrSocket =>
      resultOrSocket.right.map {
        case (readIn, writeOut, after) => (e:Enumerator[A], i:Iteratee[A,Unit]) => { writeOut |>> i; e |>> readIn; after() }
      }
    }))
  }
}
