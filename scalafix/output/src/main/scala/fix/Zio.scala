package fix

import zio.{ DefaultRuntime, Task }

object Zio {
  val successOperation: Task[Int] = Task.succeed(1)
  val failedOperation: Task[Nothing] = Task.fail(new Exception("bad"))

  val runtime: DefaultRuntime = new DefaultRuntime {}
  val result: Int = runtime.unsafeRun(successOperation)
}
