package co.bbt.ref.grpc.config

import co.bbt.ref.infrastructure.persistence.config.DBConf
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class AppConfig(database: DBConf, grpc: GrpcConf)

object AppConfig {
  implicit val appConfigDec: Decoder[AppConfig] = deriveDecoder[AppConfig]
}
