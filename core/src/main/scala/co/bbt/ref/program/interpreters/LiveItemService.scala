package co.bbt.ref.program.interpreters

import cats.MonadError
import cats.syntax.applicative._
import cats.syntax.apply._
import co.bbt.ref.domain.item.algebras.{ItemRepository, ItemService, ItemValidation}
import co.bbt.ref.domain.item.{Item, ItemID}
import co.bbt.ref.domain.{DomainError, ItemNotFound}

final class LiveItemService[F[_]: MonadError[*[_], DomainError]: ItemRepository: ItemValidation] private
  extends ItemService[F] {
  override def createItem(item: Item): F[Item] =
    ItemValidation[F].notExist(item.id) *> ItemRepository[F].create(item)

  override def updateItem(item: Item): F[Item] =
    ItemValidation[F].exist(item.id) *> ItemRepository[F].update(item)

  override def findItem(itemID: ItemID): F[Item] =
    ItemRepository[F]
      .find(itemID)
      .getOrElseF(MonadError[F, DomainError].raiseError(ItemNotFound.of(itemID)))

  override def findAllItems: F[List[Item]] =
    ItemRepository[F].findAll
      .getOrElseF(List.empty[Item].pure[F])

  override def deleteItem(itemID: ItemID): F[Unit] =
    ItemValidation[F].exist(itemID) *> ItemRepository[F].delete(itemID)
}

object LiveItemService {
  def apply[F[_]: MonadError[*[_], DomainError]: ItemRepository: ItemValidation](): LiveItemService[F] =
    new LiveItemService()
}
