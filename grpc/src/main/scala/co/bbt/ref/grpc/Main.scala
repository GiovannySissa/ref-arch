package co.bbt.ref.grpc

import java.util.concurrent.TimeUnit

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, Resource, Timer}
import cats.syntax.apply._
import cats.syntax.functor._
import co.bbt.ref.grpc.config.AppConfig
import co.bbt.ref.grpc.services.ItemSvcImpl
import co.bbt.ref.infrastructure.persistence.doobie.Transactor
import co.bbt.ref.program.interpreters.LiveItemPostgresRepository
import co.bbt.ref.program.modules.{LiveRepository, LiveService, LiveValidation}
import co.bbt.ref.proto.Item.ItemServiceFs2Grpc
import io.circe.config.parser
import io.grpc.netty.NettyServerBuilder
import monix.eval.{Task, TaskApp}
import org.lyranthe.fs2_grpc.java_runtime.syntax.all.fs2GrpcSyntaxServerBuilder
import com.olegpy.meow.hierarchy._
import io.grpc.Server

object Main extends TaskApp {
  def run(args: List[String]): Task[ExitCode] =
    ResourcesApp.create.use { server => Task.eval(server.start()) *> Task.never.as(ExitCode.Success) }

}

object ResourcesApp {

  def create[F[_]: ConcurrentEffect: ContextShift: Timer]: Resource[F, Server] =
    for {
      config <- Resource.liftF(parser.decodePathF[F, AppConfig]("co.bbt.ref"))
      xa     <- Transactor.acquire(config.database)
      repository = LiveRepository[F](LiveItemPostgresRepository[F](xa))
      validation = LiveValidation[F](repository)
      service    = LiveService[F](repository, validation)
      grpcServiceImpl = ItemServiceFs2Grpc.bindService(
        ItemSvcImpl[F](service)
      )
      server <- NettyServerBuilder
        .forPort(config.grpc.port.number)
        .addService(grpcServiceImpl)
        .keepAliveTimeout(config.grpc.timeout.value, TimeUnit.SECONDS)
        .resource[F]
    } yield server

}
