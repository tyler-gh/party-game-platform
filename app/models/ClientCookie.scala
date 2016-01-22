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

  case object NAME extends Value[String](0, "name") {
    override def extractValue(cookie: Cookie): String = {
      cookie.value
    }
  }

  case object GAME extends Value[String](1, "game") {
    override def extractValue(cookie: Cookie): String = {
      cookie.value
    }
  }

  case object ID extends Value[Long](2, "id") {
    override def extractValue(cookie: Cookie): Long = {
      cookie.value.toLong
    }
  }




}
