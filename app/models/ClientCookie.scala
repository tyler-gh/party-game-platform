package models

import play.api.mvc.{Cookie, Cookies}

case class ClientCookie[T](value: T)
object ClientCookie {

  sealed abstract class Value[V](val id: Int, val name: String) {
    protected def extractValue(cookie: Cookie): V

    def getCookie(cookies: Cookies): Option[ClientCookie[V]] = {
      cookies.get(name).map(extractValue).map(new ClientCookie[V](_))
    }

    def createCookie(value: V): Cookie = {
      Cookie(name, value.toString)
    }
  }

  case object USER_NAME extends Value[String](0, "user_name") {
    override def extractValue(cookie: Cookie): String = {
      cookie.value
    }
  }

  case object USER_ID extends Value[Int](2, "user_id") {
    override def extractValue(cookie: Cookie): Int = {
      cookie.value.toInt
    }
  }

  case object GAME_INSTANCE_ID extends Value[String](1, "game_instance_id") {
    override def extractValue(cookie: Cookie): String = {
      cookie.value
    }
  }

  case object GAME_ID extends Value[String](1, "game_id") {
    override def extractValue(cookie: Cookie): String = {
      cookie.value
    }
  }





}
