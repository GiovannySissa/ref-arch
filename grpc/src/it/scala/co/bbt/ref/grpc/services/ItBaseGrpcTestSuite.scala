package co.bbt.ref.grpc.services

import java.util.concurrent.Executors

import cats.effect.{IO, Resource}
import co.bbt.ref.grpc.env.GrpcITEnv
import co.bbt.ref.grpc.generators.GrpcGenerators
import minitest.TestSuite

import scala.concurrent.ExecutionContext

trait ItBaseGrpcTestSuite extends TestSuite[Resource[IO, GrpcITEnv[IO]]] with GrpcGenerators {

  val executor              = Executors.newFixedThreadPool(3)
  implicit val contextShift = IO.contextShift(ExecutionContext.fromExecutor(executor))

  override def setup(): Resource[IO, GrpcITEnv[IO]] = GrpcITEnv[IO]

  override def tearDown(env: Resource[IO, GrpcITEnv[IO]]): Unit = env.use(_ => IO.delay(())).unsafeRunSync()

}
