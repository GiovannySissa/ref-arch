package co.bbt.ref.infrastructure.persistence.doobie

import cats.effect.{Async, Blocker, ContextShift, Resource}
import co.bbt.ref.infrastructure.persistence.config.DBConf
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

object Transactor {
  def acquire[F[_]: Async: ContextShift](dbConfig: DBConf): Resource[F, HikariTransactor[F]] =
    for {
      connectEC <- ExecutionContexts.fixedThreadPool[F](
        dbConfig.connections.poolSize
      )
      transactionEC <- ExecutionContexts.cachedThreadPool[F]
      transactor <- HikariTransactor.newHikariTransactor[F](
        driverClassName = dbConfig.driver.className,
        url = dbConfig.address.url,
        user = dbConfig.user.userName.value,
        pass = dbConfig.user.password.plainText,
        connectEC = connectEC,
        blocker = Blocker.liftExecutionContext(transactionEC)
      )
    } yield transactor

}
