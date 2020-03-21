package co.bbt.ref.infrastructure.env

import cats.effect.{Async, ContextShift, Resource}
import co.bbt.ref.infrastructure.persistence.config.DBConf
import co.bbt.ref.infrastructure.persistence.doobie.TestTransactor
import co.bbt.ref.program.interpreters.LiveItemPostgresRepository
import co.bbt.ref.program.modules.{LiveRepository, LiveService, LiveValidation, Validation}
import com.olegpy.meow.hierarchy._
import com.typesafe.config.Config
import io.circe.Decoder
import io.circe.config.parser.decodePathF

@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
object ITResources {

  def configResource[F[_]: Async, C: Decoder](path: String)(implicit config: Config): Resource[F, C] =
    Resource.liftF(decodePathF[F, C](config, path))

  def serviceResource[F[_]: Async: ContextShift](implicit config: Config): Resource[F, LiveService[F]] =
    for {
      repository <- repositoryResource[F]
      validation <- validationResource[F]
    } yield LiveService[F](repository = repository, validation)

  def repositoryResource[F[_]: Async: ContextShift](implicit config: Config): Resource[F, LiveRepository[F]] =
    for {
      appConf <- configResource[F, DBConf]("it.db")
      xa      <- TestTransactor.create[F](appConf)
    } yield LiveRepository[F](LiveItemPostgresRepository[F](xa))

  def validationResource[F[_]: Async: ContextShift](implicit config: Config): Resource[F, Validation[F]] =
    repositoryResource[F].map(LiveValidation[F](_))
}
