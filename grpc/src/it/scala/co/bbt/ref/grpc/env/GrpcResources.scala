package co.bbt.ref.grpc.env

import cats.effect.{Async, ConcurrentEffect, ContextShift, Resource}
import co.bbt.ref.grpc.services.ItemSvcImpl
import co.bbt.ref.infrastructure.env.ITResources.{repositoryResource, validationResource}
import co.bbt.ref.program.modules.LiveService
import co.bbt.ref.proto.Item.ItemServiceFs2Grpc
import com.olegpy.meow.hierarchy._
import com.typesafe.config.{Config, ConfigFactory}
import io.grpc.inprocess.{InProcessChannelBuilder, InProcessServerBuilder}
import io.grpc.util.MutableHandlerRegistry
import io.grpc.{ManagedChannel, Metadata, Server}

object GrpcResources {

  implicit val grpcConf: Config = ConfigFactory.parseResources("application.conf")

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

  def channelResource[F[_]: ConcurrentEffect]: Resource[F, ItemServiceFs2Grpc[F, Metadata]] =
    Resource.pure[F, ManagedChannel](InProcessChannelBuilder.forName(serverName).directExecutor.build).map(
      channel =>
        ItemServiceFs2Grpc.stub[F](channel)
    )
}
