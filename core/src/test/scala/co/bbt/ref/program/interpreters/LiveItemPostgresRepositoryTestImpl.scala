package co.bbt.ref.program.interpreters

import cats.effect.{Async, ContextShift, Resource}
import co.bbt.ref.infrastructure.persistence.doobie.TestTransactor
import co.bbt.ref.program.modules.LiveRepository

object LiveItemPostgresRepositoryTestImpl {
  def apply[F[_]: Async: ContextShift]: Resource[F, LiveRepository[F]] =
    for {
      xa <- TestTransactor.create[F]
      postgresRepository = LiveItemPostgresRepository[F](xa)
      liveRepository     = LiveRepository[F](postgresRepository)
    } yield liveRepository
}
