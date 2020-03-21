package co.bbt.ref.grpc

import cats.data.ValidatedNel
import cats.syntax.apply._
import cats.syntax.either._
import cats.syntax.option._
import co.bbt.ref.domain.item.{Item, ItemID}
import co.bbt.ref.domain.{InvalidInput, MissingField}
import co.bbt.ref.ops.Transformer
import co.bbt.ref.proto.Item.{ItemIdProto, ItemProto}

package object services {

  private[services] implicit def mapToDomain: Transformer[ItemProto, ValidatedNel[InvalidInput, Item]] =
    proto => {
      (
        checkField(proto.id)("Item Id"),
        checkField(proto.name)("Item name"),
        checkField(proto.description)("Item description"),
        checkField(proto.price)("Item price"),
        checkField(proto.category)("Item category")
      ).mapN((_, _, _, _, _)) andThen {
        case (id, name, description, price, category) =>
          Item(
            id = id,
            name = name,
            description = description,
            price = price,
            category = category
          )
      }
    }

  private[services] implicit def mapToProto: Transformer[Item, ItemProto] =
    item =>
      ItemProto(
        id = item.id.value.some,
        name = item.name.value.some,
        description = item.description.value.some,
        price = item.price.value.amount.longValue().some,
        category = item.category.name.some
      )

  private[services] implicit def idMapToDomain: Transformer[ItemIdProto, ValidatedNel[InvalidInput, ItemID]] =
    proto => checkField(proto.id)("Item Id").andThen(ItemID(_).toValidatedNel)

  private[services] def checkField[ProtoField](field: Option[ProtoField])(
    name: String): ValidatedNel[InvalidInput, ProtoField] =
    field.toValidNel(MissingField.of(name))

}
