package co.bbt.ref.program.interpreters

import co.bbt.ref.domain.item.{Item, ItemID}
import co.bbt.ref.domain.item.algebras.ItemRepository
import doobie.util.transactor.Transactor

//todo create SQL statements
final class LiveItemPostgresRepository[F[_]] private (private val xa: Transactor[F]) extends ItemRepository[F] {
  override def create(item: Item): F[Item] = ???

  override def update(item: Item): F[Item] = ???

  override def find(itemID: ItemID): F[Item] = ???

  override def findAll: F[List[Item]] = ???

  override def delete(itemID: ItemID): F[Unit] = ???
}

object LiveItemPostgresRepository {
  def apply[F[_]](xa: Transactor[F]): LiveItemPostgresRepository[F] = new LiveItemPostgresRepository(xa)
}
