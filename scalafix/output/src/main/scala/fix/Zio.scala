package fix

import zio.Task

object Zio {
  val successOperation: Task[Int] = Task.succeed(1)
  val failedOperation: Task[Nothing] = Task.fail(new Exception("bad"))
}
