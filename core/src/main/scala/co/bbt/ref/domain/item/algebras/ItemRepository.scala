package co.bbt.ref.domain.item.algebras

import cats.data.OptionT
import co.bbt.ref.domain.item.{Item, ItemID}

trait ItemRepository[F[_]] {
  def create(item: Item): F[Item]
  def update(item: Item): F[Item]
  def find(itemID: ItemID): OptionT[F, Item]
  def findAll: OptionT[F, List[Item]]
  def delete(itemID: ItemID): F[Unit]
}
