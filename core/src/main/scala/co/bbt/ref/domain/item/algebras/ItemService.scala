package co.bbt.ref.domain.item.algebras

import co.bbt.ref.domain.item.{Item, ItemID}

trait ItemService[F[_]] {
  def createItem(item: Item): F[Item]
  def updateItem(item: Item): F[Item]
  def findItem(itemID: ItemID): F[Item]
  def findAllItems: F[List[Item]]
  def deleteItem(itemID: ItemID): F[Unit]
}

object ItemService {
  def apply[F[_]: ItemService](): ItemService[F] = implicitly
}
