package co.bbt.ref.grpc.config

import co.bbt.ref.infrastructure.persistence.config.DBConf
import co.bbt.ref.infrastructure.persistence.config.DBConf._
import com.typesafe.config.ConfigFactory
import io.circe
import io.circe.Decoder
import io.circe.config.parser
import minitest.SimpleTestSuite

object GrpcConfTest extends SimpleTestSuite {

  test("Read config from file") {

    assert(readConf[GrpcConf]("co.bbt.ref.grpc").isRight, "GrpcConf not in application.conf")
    assert(readConf[DBConf]("co.bbt.ref.database").isRight, "DBConf not in application.conf")
    assert(readConf[AppConfig]("co.bbt.ref").isRight, "AppConfig not in application.conf")

  }

  private def readConf[T: Decoder](path: String): Either[circe.Error, T] =
    parser
      .decodePath[T](ConfigFactory.parseResources("application.conf"), path)

}
