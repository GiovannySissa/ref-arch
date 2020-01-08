package co.bbt.ref.infrastructure.persistence.doobie

import cats.effect.{Async, Blocker, ContextShift, Resource}
import doobie.h2.H2Transactor
import doobie.util.ExecutionContexts

//todo read configuration use pure config

object TestTransactor {
  def create[F[_]: Async: ContextShift]: Resource[F, H2Transactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](10) //(config.connections.poolSize)
      te <- ExecutionContexts.cachedThreadPool[F]
      xa <- H2Transactor.newH2Transactor[F](
        "jdbc:h2:mem:S4N;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/1.sql'", //      config.address.url,
        "user", //config.user.userName.value,
        "password", //config.user.password.plainText,
        ce,
        Blocker.liftExecutionContext(te)
      )
    } yield xa
}
