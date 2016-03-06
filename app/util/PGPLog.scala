package util

object PGPLog {

  implicit class Logger(a: Any) {
    def printErrLn() {
      System.err.println(a)
    }
  }
}
