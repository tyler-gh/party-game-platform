package models

import java.util.concurrent.ConcurrentHashMap


object Games {
  private val games = new ConcurrentHashMap[String, Game]()

  def createGame(id: String): Option[Game] = {
    if(games.containsKey(id)) {
      None
    } else {
      val newGame = new Game(id)
      games.put(id, newGame)
      Some(newGame)
    }
  }

  def getGame(id: String): Option[Game] = {
    println(games)
    Option(games.get(id))
  }

}
