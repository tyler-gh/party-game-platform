package models.game

import javax.script.ScriptEngine

import jdk.nashorn.api.scripting.NashornScriptEngineFactory

/**
  *
  */
object GameScriptEngine {
  def getNewEngine: ScriptEngine = {
    val engine = new NashornScriptEngineFactory().getScriptEngine("--no-java")
    engine
  }
}
