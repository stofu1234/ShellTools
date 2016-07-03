package jp.co.stofu.ShellTools

import java.util.stream.StreamSupport
import java.util.Spliterator
import java.util.Spliterators

object ProcessStream {
   def create(args: String*): java.util.stream.Stream[String] = {
      val pi = new ProcessIterator(args:_*)
      return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                   pi, Spliterator.ORDERED | Spliterator.NONNULL), false)
   }
}

