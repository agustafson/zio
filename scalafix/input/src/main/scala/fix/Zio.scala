/*
rule = Zio
*/
package fix

import cats.effect.IO

object Zio {
  val successOperation: IO[Int] = IO.pure(1)
  val failedOperation: IO[Nothing] = IO.raiseError(new Exception("bad"))
}
