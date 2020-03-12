package co.bbt.ref.grpc.services

import java.util.concurrent.Executors

import cats.effect.{IO, Resource}
import minitest.TestSuite

import scala.concurrent.ExecutionContext

class ItBaseGrpcTestSuite extends TestSuite[Resource[IO, GrpcITEnv[IO]]] {

  val executor = Executors.newFixedThreadPool(3)
  implicit val contextShift =  IO.contextShift(ExecutionContext.fromExecutor(executor))

  override def setup(): Resource[IO, GrpcITEnv[IO]] = GrpcITEnv[IO]

  override def tearDown(env: Resource[IO, GrpcITEnv[IO]]): Unit = env.use(_ => IO.delay(())).unsafeRunSync()

}
