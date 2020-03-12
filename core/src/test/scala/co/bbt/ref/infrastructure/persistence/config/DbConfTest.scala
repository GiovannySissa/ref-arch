package co.bbt.ref.infrastructure.persistence.config

import com.typesafe.config.ConfigFactory
import io.circe
import io.circe.Decoder
import io.circe.config.parser
import minitest.SimpleTestSuite
import DBConf._

object DbConfTest extends SimpleTestSuite {

  test("Read config from file") {

    assert(readConf[DBAddress]("co.bbt.ref.database.address").isRight, "DB Address not in application.conf")
    assert(readConf[DBDriver]("co.bbt.ref.database.driver").isRight, "DB Driver not in application.conf")
    assert(readConf[DBUser]("co.bbt.ref.database.user").isRight, "DB User not in application.conf")
    assert(readConf[DBPassword]("co.bbt.ref.database.user.password").isRight, "DB password not in application.conf")
    assert(readConf[DBUserName]("co.bbt.ref.database.user.userName").isRight, "DB user name not in application.conf")
    assert(readConf[DBConnections]("co.bbt.ref.database.connections").isRight, "DB connections not in application.conf")
    assert(readConf[DBConf]("co.bbt.ref.database").isRight, "DBConf application.conf")

  }

  private def readConf[T: Decoder](path: String): Either[circe.Error, T] =
    parser
      .decodePath[T](ConfigFactory.parseResources("application.conf"), path)

}
