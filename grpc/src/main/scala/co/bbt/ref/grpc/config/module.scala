package co.bbt.ref.grpc.config

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class GrpcHost(ip: String) extends AnyVal
final case class GrpcPort(number: Int) extends AnyVal
final case class GrpcTimeout(value: Long) extends AnyVal
final case class GrpcConf(host: GrpcHost, port: GrpcPort, timeout: GrpcTimeout)

object GrpcConf {
  implicit val grpcHostDec: Decoder[GrpcHost]       = deriveDecoder[GrpcHost]
  implicit val grpcPortDec: Decoder[GrpcPort]       = deriveDecoder[GrpcPort]
  implicit val GrpcConfDec: Decoder[GrpcConf]       = deriveDecoder[GrpcConf]
  implicit val grpcTimeoutDec: Decoder[GrpcTimeout] = deriveDecoder[GrpcTimeout]
}
