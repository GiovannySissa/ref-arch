package co.bbt.ref.infrastructure.persistence.doobie

import cats.effect.{Async, Blocker, ContextShift, Resource}
import co.bbt.ref.infrastructure.persistence.config.DBConf
import doobie.h2.H2Transactor
import doobie.util.ExecutionContexts

object TestTransactor {
  def create[F[_]: Async: ContextShift](config: DBConf): Resource[F, H2Transactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](config.connections.poolSize)
      te <- ExecutionContexts.cachedThreadPool[F]
      xa <- H2Transactor.newH2Transactor[F](
        config.address.url,
        config.user.userName.value,
        config.user.password.plainText,
        ce,
        Blocker.liftExecutionContext(te)
      )
    } yield xa
}
