package co.bbt.ref.program.interpreters

import cats.effect.{Async, ContextShift, Resource}
import co.bbt.ref.infrastructure.persistence.config.DBConf
import co.bbt.ref.infrastructure.persistence.doobie.TestTransactor
import co.bbt.ref.program.modules.LiveRepository
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.config.parser

object LiveItemPostgresRepositoryTestImpl {

  val conf: Config = ConfigFactory.parseResources("test.conf")
  def apply[F[_]: Async: ContextShift]: Resource[F, LiveRepository[F]] =
    for {
      config <- Resource.liftF(parser.decodePathF[F, DBConf](conf, "test.co.bbt.ref.database"))

      xa <- TestTransactor.create[F](config)
      postgresRepository = LiveItemPostgresRepository[F](xa)
      liveRepository     = LiveRepository[F](postgresRepository)
    } yield liveRepository
}
