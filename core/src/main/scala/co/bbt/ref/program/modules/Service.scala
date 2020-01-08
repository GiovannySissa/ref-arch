package co.bbt.ref.program.modules

import cats.MonadError
import co.bbt.ref.domain.DomainError
import co.bbt.ref.domain.item.algebras.{ItemRepository, ItemService, ItemValidation}
import co.bbt.ref.program.interpreters.LiveItemService

trait Service[F[_]] {
  def itemService: ItemService[F]
}

final class LiveService[F[_]] private (private val svc: ItemService[F]) extends Service[F] {
  override def itemService: ItemService[F] = svc
}

object LiveService {
  def apply[F[_]: MonadError[*[_], DomainError]](
    repository: Repository[F],
    validation: Validation[F]): LiveService[F] = {
    implicit val r: ItemRepository[F] = repository.itemRepository
    implicit val v: ItemValidation[F] = validation.itemValidation

    new LiveService(LiveItemService[F])
  }
}
