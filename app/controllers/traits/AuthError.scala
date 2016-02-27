package controllers.traits

import play.api.libs.json._
import play.api.mvc.{Results, Result}

trait AuthError[R] {

  def missingCookie(): R

  def gameDNE(): R

  def leftGame(): R

  def notAMemberOfGame(): R
}

class FlatAuthError[T](m: => T) extends AuthError[T] {
  override def missingCookie(): T = m

  override def notAMemberOfGame(): T = m

  override def leftGame(): T = m

  override def gameDNE(): T = m
}

object AuthError {
  implicit val mvcResultAuthError: AuthError[Result] = new AuthError[Result] {

    def error(e: JsValue): Result = {
      Results.BadRequest(Json.obj(("error", e)))
    }

    override def missingCookie(): Result = {
      error(JsString("You must have created or joined a game before you can connect to a game"))
    }

    override def notAMemberOfGame(): Result = {
      error(JsString("You are not a member of this game"))
    }

    override def leftGame(): Result = {
      error(JsString("You are no longer a member of this game. Rejoin the game if you would like to connect to it."))
    }

    override def gameDNE(): Result = {
      error(JsString("This game no longer exists"))
    }

  }
}
