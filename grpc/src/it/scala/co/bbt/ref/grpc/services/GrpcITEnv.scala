package co.bbt.ref.grpc.services

import cats.effect.{Async, ContextShift, Resource}
import co.bbt.ref.infrastructure.env.ITResources._
import co.bbt.ref.program.modules.LiveService
import com.olegpy.meow.hierarchy._
import com.typesafe.config.{Config, ConfigFactory}
import io.grpc.inprocess.{InProcessChannelBuilder, InProcessServerBuilder}
import io.grpc.util.MutableHandlerRegistry
import io.grpc.{ManagedChannel, Server}

final case class GrpcITEnv[F[_]] private (service: LiveService[F], grpcImpl: ItemSvcImpl[F])

object GrpcITEnv {

  private implicit val grpcConf: Config = ConfigFactory.parseResources("application.conf")

  def apply[F[_]: Async: ContextShift]: Resource[F, GrpcITEnv[F]] =
    for {
      service     <- serviceResource[F]
      grpcService <- grpcServiceResource
    } yield new GrpcITEnv(service, grpcService)

  def grpcServiceResource[F[_]: Async: ContextShift]: Resource[F, ItemSvcImpl[F]] =
    for {
      liveRepo       <- repositoryResource[F]
      liveValidation <- validationResource[F]
    } yield ItemSvcImpl[F](LiveService[F](liveRepo, liveValidation))

  private val serverName: String = InProcessServerBuilder.generateName

  private val handlerRegistry: MutableHandlerRegistry = new MutableHandlerRegistry

  def serverResource[F[_]: Async]: Resource[F, Server] =
    Resource.pure[F, Server](
      InProcessServerBuilder
        .forName(serverName)
        .directExecutor
        .fallbackHandlerRegistry(handlerRegistry)
        .build
        .start)

  def channelResource[F[_]: Async]: Resource[F, ManagedChannel] =
    Resource.pure[F, ManagedChannel](InProcessChannelBuilder.forName(serverName).directExecutor.build)

}
