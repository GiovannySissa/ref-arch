package co.bbt.ref.program.interpreters

import cats.data.OptionT
import cats.effect.Async
import cats.instances.list._
import cats.instances.option._
import cats.syntax.functor._
import cats.syntax.traverse._
import co.bbt.ref.domain.item.algebras.ItemRepository
import co.bbt.ref.domain.item.{Item, ItemID}
import co.bbt.ref.infrastructure.persistence.doobie.ItemSQL
import co.bbt.ref.infrastructure.persistence.rows.ItemRow
import co.bbt.ref.ops.mapper._
import doobie.implicits._
import doobie.util.transactor.Transactor

final class LiveItemPostgresRepository[F[_]: Async] private (private val xa: Transactor[F]) extends ItemRepository[F] {
  override def create(item: Item): F[Item] =
    ItemSQL
      .insert(item.map[ItemRow])
      .run
      .map(_ => item)
      .transact(xa)

  override def update(item: Item): F[Item] =
    ItemSQL
      .update(item.map[ItemRow])
      .run
      .map(_ => item)
      .transact(xa)

  override def find(itemID: ItemID): OptionT[F, Item] =
    OptionT(
      ItemSQL
        .select(itemID.value)
        .option
        .transact(xa)
    ) subflatMap (_.mapV[Item].toOption)

  override def findAll: OptionT[F, List[Item]] =
    OptionT(
      ItemSQL.selectAll
        .transact(xa)
        .map(_.traverse(itemRow => itemRow.mapV[Item].toOption))
    )

  override def delete(itemID: ItemID): F[Unit] =
    ItemSQL
      .delete(itemID.value)
      .run
      .map(_ => ())
      .transact(xa)
}

object LiveItemPostgresRepository {
  def apply[F[_]: Async](xa: Transactor[F]): LiveItemPostgresRepository[F] = new LiveItemPostgresRepository(xa)
}
