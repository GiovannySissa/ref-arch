package co.bbt.ref.program.modules

import co.bbt.ref.domain.item.algebras.ItemRepository

trait Repository[F[_]] {
  def itemRepository: ItemRepository[F]
}

final class LiveRepository[F[_]] private (private val itemRepo: ItemRepository[F]) extends Repository[F] {
  override def itemRepository: ItemRepository[F] = itemRepo
}

object LiveRepository {
  def apply[F[_]](itemRepo: ItemRepository[F]): LiveRepository[F] = new LiveRepository(itemRepo)
}
