package co.bbt.ref.domain.item

import cats.data.ValidatedNel
import cats.instances.either._
import cats.syntax.apply._
import cats.syntax.either._
import co.bbt.ref.domain.{InvalidInput, InvalidItemId, InvalidName}
import co.bbt.ref.ops.validation._

final case class Item private (id: ItemID, name: ItemName)

object Item {
  def apply(id: String, name: String): ValidatedNel[InvalidInput, Item] =
    (
      ItemID(id).toValidatedNel[InvalidInput],
      ItemName(name).toValidatedNel[InvalidInput]
    ).mapN(new Item(_, _))
}

final case class ItemID private (value: String) extends AnyVal
object ItemID {
  def apply(value: String): Either[InvalidItemId, ItemID] =
    value
      .condF(_.nonEmpty)(InvalidItemId())
      .map(new ItemID(_))
}

final case class ItemName private (name: String) extends AnyVal
object ItemName {
  def apply(name: String): Either[InvalidName, ItemName] =
    name
      .condF(_.nonEmpty)(InvalidName())
      .map(new ItemName(_))
}
