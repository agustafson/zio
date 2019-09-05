/*
rule = Zio
*/
package fix

import cats.effect.{ExitCode, IO, IOApp}

object Zio {
  val successOperation: IO[Int] = IO.pure(1)
  val failedOperation: IO[Nothing] = IO.raiseError(new Exception("bad"))
}

object ZioApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    if (args.nonEmpty) IO.pure(ExitCode.Success) else IO.pure(ExitCode.Error)
}
