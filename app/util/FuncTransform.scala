package util

import java.util.function.{BiConsumer, Consumer}


object FuncTransform {
  implicit def toConsumer[A](function: A => Unit): Consumer[A] = new Consumer[A] {
    override def accept(arg: A) = function(arg)
  }

  implicit def toBiConsumer[A, B](function: (A,B) => Unit): BiConsumer[A, B] = new BiConsumer[A, B] {
    override def accept(arg1: A, arg2: B) = function(arg1, arg2)
  }
}
