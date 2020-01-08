package co.bbt.ref.domain.item

import cats.data.ValidatedNel
import cats.instances.either._
import cats.syntax.apply._
import cats.syntax.either._
import co.bbt.ref.domain._
import co.bbt.ref.ops.validation._
import squants.market.{Money, USD}

final case class Item private (
  id: ItemID,
  name: ItemName,
  description: ItemDescription,
  price: Price,
  category: Category)

object Item {
  def apply(
    id: String,
    name: String,
    description: String,
    price: BigDecimal,
    category: String): ValidatedNel[InvalidInput, Item] =
    (
      ItemID(id).toValidatedNel[InvalidInput],
      ItemName(name).toValidatedNel[InvalidInput],
      ItemDescription(description).toValidatedNel,
      Price(price).toValidatedNel,
      Category(category).toValidatedNel
    ).mapN(new Item(_, _, _, _, _))
}

final case class ItemID private (value: String) extends AnyVal
object ItemID {
  def apply(value: String): Either[InvalidItemId, ItemID] =
    value
      .condF(_.nonEmpty)(InvalidItemId())
      .map(new ItemID(_))
}

final case class ItemName private (value: String) extends AnyVal
object ItemName {
  def apply(name: String): Either[InvalidName, ItemName] =
    name
      .condF(_.nonEmpty)(InvalidName())
      .map(new ItemName(_))
}

final case class ItemDescription private (value: String) extends AnyVal
object ItemDescription {
  def apply(value: String): Either[EmptyDescription, ItemDescription] =
    value
      .condF(_.nonEmpty)(EmptyDescription())
      .map(new ItemDescription(_))
}

final case class Price private (value: Money) extends AnyVal
object Price {
  def apply(value: BigDecimal): Either[NegativePrice, Price] =
    value
      .condF(_ >= 0)(NegativePrice())
      .map(pr => new Price(USD(pr)))
}

final case class Category private (name: String) extends AnyVal
object Category {
  def apply(name: String): Either[InvalidItemCategory, Category] =
    name
      .condF(_.nonEmpty)(InvalidItemCategory())
      .map(new Category(_))
}
