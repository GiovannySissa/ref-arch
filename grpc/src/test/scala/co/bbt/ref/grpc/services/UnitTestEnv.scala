package co.bbt.ref.grpc.services

import cats.effect.{Async, Resource}
import cats.syntax.functor._
import co.bbt.ref.infrastructure.persistence.memory.ItemInMemoryRepository
import co.bbt.ref.program.modules.{LiveRepository, LiveService, LiveValidation}
import com.olegpy.meow.hierarchy._

object UnitTestEnv {

  def create[F[_]: Async]: Resource[F, ItemSvcImpl[F]] = {
    Resource.liftF(
      ItemInMemoryRepository
        .makeRef[F]
        .map(ref => {
          val repo: ItemInMemoryRepository[F]   = ItemInMemoryRepository[F](ref)
          val liveRepo: LiveRepository[F]       = LiveRepository[F](repo)
          val liveValidation: LiveValidation[F] = LiveValidation[F](liveRepo)
          ItemSvcImpl[F](LiveService[F](liveRepo, liveValidation))
        }))
  }

}
