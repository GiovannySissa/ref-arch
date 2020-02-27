package co.bbt.ref.infrastructure.persistence.config

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class DBAddress(url: String) extends AnyVal
final case class DBDriver(className: String) extends AnyVal
final case class DBPassword(plainText: String) extends AnyVal
final case class DBUserName(value: String) extends AnyVal
final case class DBConnections(poolSize: Int) extends AnyVal
final case class DBUser(userName: DBUserName, password: DBPassword)
final case class DBConf(
  address: DBAddress,
  driver: DBDriver,
  user: DBUser,
  connections: DBConnections
)

object DBConf {
  implicit val addressConfigDec: Decoder[DBAddress] = deriveDecoder[DBAddress]
  implicit val driverConfigDec: Decoder[DBDriver]   = deriveDecoder[DBDriver]
  implicit val passwordConfigDec: Decoder[DBPassword] =
    deriveDecoder[DBPassword]
  implicit val userNameConfigDec: Decoder[DBUserName] =
    deriveDecoder[DBUserName]
  implicit val userConfigDec: Decoder[DBUser] = deriveDecoder[DBUser]
  implicit val poolConfigDec: Decoder[DBConnections] =
    deriveDecoder[DBConnections]
  implicit val dbConfigDec: Decoder[DBConf] = deriveDecoder[DBConf]
}
