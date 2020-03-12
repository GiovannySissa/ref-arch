package co.bbt.ref.program.modules

import cats.MonadError
import co.bbt.ref.domain.ValidationError
import co.bbt.ref.domain.item.algebras.{ItemRepository, ItemValidation}
import co.bbt.ref.program.interpreters.LiveItemValidation

trait Validation[F[_]] {
  def itemValidation: ItemValidation[F]
}

final class LiveValidation[F[_]] private (private val validation: ItemValidation[F]) extends Validation[F] {
  override def itemValidation: ItemValidation[F] = validation
}

object LiveValidation {
  def apply[F[_]: MonadError[*[_], ValidationError]](repository: Repository[F]): Validation[F] = {
    implicit val itemRepository: ItemRepository[F] = repository.itemRepository
    new LiveValidation[F](LiveItemValidation[F])
  }
}
