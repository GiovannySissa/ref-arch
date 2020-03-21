package co.bbt.ref.grpc.services

import cats.effect.{IO, Resource}
import co.bbt.ref.grpc.generators.GrpcGenerators
import minitest.TestSuite

abstract class BaseGrpcTestSuite extends TestSuite[Resource[IO, ItemSvcImpl[IO]]] with GrpcGenerators {

  override def setup(): Resource[IO, ItemSvcImpl[IO]] = UnitTestEnv.create[IO]

  override def tearDown(env: Resource[IO, ItemSvcImpl[IO]]): Unit =
    env.use(_ => IO.unit).unsafeRunSync
}
