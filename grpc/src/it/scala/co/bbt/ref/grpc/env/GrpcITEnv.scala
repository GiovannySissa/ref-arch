package co.bbt.ref.grpc.env

import cats.effect.{Async, ConcurrentEffect, ContextShift, Resource}
import co.bbt.ref.grpc.services.ItemSvcImpl
import co.bbt.ref.infrastructure.env.ITResources._
import co.bbt.ref.program.modules.LiveService
import co.bbt.ref.proto.Item.ItemServiceFs2Grpc
import io.grpc.{ManagedChannel, Metadata}

final case class GrpcITEnv[F[_]] private (
  service: LiveService[F],
  grpcImpl: ItemSvcImpl[F],
  client: ItemServiceFs2Grpc[F, Metadata])

object GrpcITEnv {

  import GrpcResources._

  def apply[F[_]: ConcurrentEffect: ContextShift]: Resource[F, GrpcITEnv[F]] =
    for {
      service     <- serviceResource[F]
      grpcService <- grpcServiceResource[F]
      _           <- serverResource[F]
      client      <- channelResource[F]
    } yield new GrpcITEnv(service, grpcService, client)

}
