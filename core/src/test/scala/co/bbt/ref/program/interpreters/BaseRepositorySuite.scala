package co.bbt.ref.program.interpreters

import cats.effect.{ContextShift, IO, Resource}
import co.bbt.ref.program.modules.LiveRepository
import minitest.TestSuite

import scala.concurrent.ExecutionContext

abstract class BaseRepositorySuite extends TestSuite[Resource[IO, LiveRepository[IO]]] {
  implicit lazy val testCs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  override def setup(): Resource[IO, LiveRepository[IO]] =
    LiveItemPostgresRepositoryTestImpl[IO]

  override def tearDown(env: Resource[IO, LiveRepository[IO]]): Unit =
    env.use(_ => IO.unit).unsafeRunSync
}
