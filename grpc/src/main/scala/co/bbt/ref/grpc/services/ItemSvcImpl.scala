package co.bbt.ref.grpc.services

import cats.effect.Async
import cats.syntax.applicativeError._
import cats.syntax.functor._
import cats.syntax.option._
import co.bbt.ref.domain.ErrorContainer
import co.bbt.ref.domain.item.algebras.ItemService
import co.bbt.ref.domain.item.{Item, ItemID}
import co.bbt.ref.ops.mapper._
import co.bbt.ref.program.modules.Service
import co.bbt.ref.proto.Item._
import com.google.protobuf.empty.Empty
import io.grpc.Metadata

final class ItemSvcImpl[F[_]: Async: ItemService] private extends ItemServiceFs2Grpc[F, Metadata] {
  override def createItem(proto: ItemProto, ctx: Metadata): F[ItemProto] =
    proto
      .mapV[Item]
      .fold(
        errors => ErrorContainer(errors.toList).raiseError[F, Item],
        ItemService[F].createItem
      )
      .map(_.map[ItemProto])

  override def updateItem(proto: ItemProto, ctx: Metadata): F[ItemProto] =
    proto
      .mapV[Item]
      .fold(
        errors => ErrorContainer(errors.toList).raiseError[F, Item],
        ItemService[F].updateItem
      )
      .map(_.map[ItemProto])

  override def findItem(proto: ItemIdProto, ctx: Metadata): F[ItemProto] =
    proto
      .mapV[ItemID]
      .fold(
        errors => ErrorContainer(errors.toList).raiseError[F, Item],
        ItemService[F].findItem
      )
      .map(_.map[ItemProto])

  override def findAll(request: Empty, ctx: Metadata): F[ItemsProto] =
    ItemService[F].findAllItems.map(res => ItemsProto(items = res.map(_.map[ItemProto])))

  override def deleteItem(proto: ItemIdProto, ctx: Metadata): F[ItemDeleteMsgProto] =
    proto
      .mapV[ItemID]
      .fold(
        errors => ErrorContainer(errors.toList).raiseError[F, ItemDeleteMsgProto],
        ItemService[F].deleteItem(_).map(_ => ItemDeleteMsgProto(message = "Item deleted!".some))
      )
}

object ItemSvcImpl {
  def apply[F[_]: Async](svc: Service[F]): ItemSvcImpl[F] = {
    implicit val itemSvc: ItemService[F] = svc.itemService
    new ItemSvcImpl
  }
}
