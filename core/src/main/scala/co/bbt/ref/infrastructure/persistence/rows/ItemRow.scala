package co.bbt.ref.infrastructure.persistence.rows

import cats.data.ValidatedNel
import co.bbt.ref.domain.InvalidInput
import co.bbt.ref.domain.item.Item
import co.bbt.ref.ops.Transformer

final case class ItemRow(id: String, name: String, description: String, price: BigDecimal, category: String)

object ItemRow {
  implicit def toDomain: Transformer[ItemRow, ValidatedNel[InvalidInput, Item]] =
    row =>
      Item(
        id = row.id,
        name = row.name,
        description = row.description,
        price = row.price,
        category = row.category
      )

  implicit def fromDomain: Transformer[Item, ItemRow] =
    item =>
      ItemRow(
        id = item.id.value,
        name = item.name.value,
        description = item.description.value,
        price = item.price.value.amount,
        category = item.category.name
      )
}
