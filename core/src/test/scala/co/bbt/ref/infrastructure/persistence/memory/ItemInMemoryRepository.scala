package co.bbt.ref.infrastructure.persistence.memory

import cats.data.OptionT
import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.syntax.functor._
import cats.syntax.option._
import co.bbt.ref.domain.item.algebras.ItemRepository
import co.bbt.ref.domain.item.{Item, ItemID}
import co.bbt.ref.infrastructure.persistence.memory.ItemInMemoryRepository.ItemRef

final class ItemInMemoryRepository[F[_]: Sync] private (ref: ItemRef[F]) extends ItemRepository[F] {
  override def create(item: Item): F[Item] =
    ref
      .modify(old => (old + (item.id -> item), item))

  override def update(item: Item): F[Item] =
    ref
      .modify(old => (old + (item.id -> item), item))

  override def find(itemID: ItemID): OptionT[F, Item] =
    OptionT(
      ref.get
        .map(items => items.get(itemID))
    )

  override def findAll: OptionT[F, List[Item]] =
    OptionT(
      ref.get
        .map(_.values.toList.some)
    )

  override def delete(itemID: ItemID): F[Unit] =
    ref
      .update(items => items - itemID)
}

object ItemInMemoryRepository {
  type ItemRepositoryRef[F[_], K, V] = Ref[F, Map[K, V]]
  type ItemRef[F[_]]                 = ItemRepositoryRef[F, ItemID, Item]

  def makeRef[F[_]: Sync]: F[ItemRef[F]]                            = Ref.of(Map.empty)
  def apply[F[_]: Sync](ref: ItemRef[F]): ItemInMemoryRepository[F] = new ItemInMemoryRepository(ref)
}
