package co.bbt.ref.program.interpreters

import cats.MonadError
import cats.syntax.applicativeError._
import cats.syntax.functor._
import co.bbt.ref.domain.item.ItemID
import co.bbt.ref.domain.item.algebras.{ItemRepository, ItemValidation}
import co.bbt.ref.domain.{ItemAlreadyExist, ItemNotFound, ValidationError}

final class LiveItemValidation[F[_]: MonadError[*[_], ValidationError]: ItemRepository] private
  extends ItemValidation[F] {
  override def exist(itemID: ItemID): F[Unit] =
    ItemRepository[F]
      .find(itemID)
      .getOrElseF(ItemNotFound.of(itemID).raiseError)
      .void

  override def notExist(itemID: ItemID): F[Unit] =
    ItemRepository[F]
      .find(itemID)
      .semiflatMap[Unit](_ => ItemAlreadyExist.of(itemID).raiseError)
      .getOrElse(())
}

object LiveItemValidation {
  def apply[F[_]: MonadError[*[_], ValidationError]: ItemRepository]: LiveItemValidation[F] = new LiveItemValidation[F]
}
