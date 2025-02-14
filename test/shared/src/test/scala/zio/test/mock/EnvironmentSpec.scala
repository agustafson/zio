package zio.test.mock

import java.util.concurrent.TimeUnit

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Random => SRandom }

import zio._
import zio.duration._
import zio.test.TestUtils.label

object EnvironmentSpec extends DefaultRuntime {

  def run(implicit ec: ExecutionContext): List[Future[(Boolean, String)]] = List(
    label(currentTime, "Clock returns time when it is set"),
    label(putStrLn, "Console writes line to output"),
    label(getStrLn, "Console reads line from input"),
    label(nextInt, "Random returns next integer when data is fed"),
    label(env, "System returns an environment variable when it is set"),
    label(property, "System returns a property when it is set "),
    label(lineSeparator, "System returns the line separator when it is set ")
  )

  def withEnvironment[E <: Throwable, A](zio: ZIO[MockEnvironment, E, A]): Future[A] =
    unsafeRunToFuture(mockEnvironmentManaged.use[Any, E, A](r => zio.provide(r)))

  def currentTime =
    withEnvironment {
      for {
        _    <- MockClock.setTime(1.millis)
        time <- clock.currentTime(TimeUnit.MILLISECONDS)
      } yield time == 1L
    }

  def putStrLn =
    withEnvironment {
      for {
        _      <- console.putStrLn("First line")
        _      <- console.putStrLn("Second line")
        output <- MockConsole.output
      } yield output == Vector("First line\n", "Second line\n")
    }

  def getStrLn =
    withEnvironment {
      for {
        _      <- MockConsole.feedLines("Input 1", "Input 2")
        input1 <- console.getStrLn
        input2 <- console.getStrLn
      } yield (input1 == "Input 1") && (input2 == "Input 2")
    }

  def nextInt =
    withEnvironment {
      for {
        _ <- MockRandom.setSeed(4491842685265857478L)
        n <- random.nextInt
      } yield n == new SRandom(4491842685265857478L).nextInt()
    }

  def env =
    withEnvironment {
      for {
        _   <- MockSystem.putEnv("k1", "v1")
        env <- system.env("k1")
      } yield env == Some("v1")
    }

  def property =
    withEnvironment {
      for {
        _   <- MockSystem.putProperty("k1", "v1")
        env <- system.property("k1")
      } yield env == Some("v1")
    }

  def lineSeparator =
    withEnvironment {
      for {
        _       <- MockSystem.setLineSeparator(",")
        lineSep <- system.lineSeparator
      } yield lineSep == ","
    }
}
