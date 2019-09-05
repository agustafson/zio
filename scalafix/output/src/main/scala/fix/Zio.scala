package fix

import zio.{ App, Task, ZIO }

object Zio {
  val successOperation: Task[Int] = Task.succeed(1)
  val failedOperation: Task[Nothing] = Task.fail(new Exception("bad"))
}

object ZioApp extends App {
  override def run(args: List[String]): ZIO[ZioApp.Environment, Nothing, Int] =
    if (args.nonEmpty) Task.succeed(0) else Task.succeed(1)
}
