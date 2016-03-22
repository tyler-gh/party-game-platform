package util

import com.typesafe.jse.Engine.JsExecutionResult

object PGPLog {

  implicit class Logger(a: Any) {
    def printErrLn() {
      System.err.println(a)
    }
  }

  implicit class JsResultLogger(jsResult: JsExecutionResult) {
    def printResult() {
      print(new String(jsResult.output.toArray, "UTF-8"))
      new String(jsResult.error.toArray, "UTF-8").printErrLn()
    }
  }

}
