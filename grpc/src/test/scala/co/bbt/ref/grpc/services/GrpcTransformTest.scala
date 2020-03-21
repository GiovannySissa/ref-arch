package co.bbt.ref.grpc.services

import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import cats.syntax.either._
import cats.syntax.option._
import co.bbt.ref.domain.MissingField
import co.bbt.ref.domain.item.{Item, ItemID}
import co.bbt.ref.grpc.generators.GrpcGenerators
import co.bbt.ref.ops.mapper._
import co.bbt.ref.proto.Item.ItemProto
import minitest.SimpleTestSuite

object GrpcTransformTest extends SimpleTestSuite with GrpcGenerators {

  test("Convert itemIdProto to itemId successful")(
    idProtoGen.sample.fold(fail("Error creating IdProto instance"))(idProto => {

      val expected = ItemID(value = idProto.id.getOrElse("")).toValidatedNel
      val received = idProto.mapV[ItemID]
      assert(received.isValid, "IdProto transformation failed")
      assertEquals(expected, received)
    })
  )

  test("An invalid IdProto can't be converted")(
    invalidIdProtoGen.sample.fold(fail("Error creating IdProto instance"))(idProto => {

      val expected = Invalid(NonEmptyList.of(MissingField.of("Item Id")))
      val received = idProto.mapV[ItemID]
      assert(received.isInvalid, "IdProto should be invalid")
      assertEquals(expected, received)
    })
  )

  test("Convert ItemProto to Item successful") {
    itemProtoGen.sample.fold(fail("Error creating ItemProto instance"))(itemProto => {
      val expected = Item(
        id = itemProto.id.getOrElse(""),
        name = itemProto.name.getOrElse(""),
        description = itemProto.description.getOrElse(""),
        price = itemProto.price.getOrElse(-1L),
        category = itemProto.category.getOrElse("")
      )
      val received = itemProto.mapV[Item]
      assert(received.isValid, "ItemProto transformation failed")
      assertEquals(expected, received)
    })
  }

  test("Convert Item to ItemProto successful") {
    (for {
      itemValidNel <- itemGenerator.sample
      item         <- itemValidNel.toOption
    } yield {
      val expected = ItemProto(
        id = item.id.value.some,
        name = item.name.value.some,
        description = item.description.value.some,
        price = item.price.value.amount.longValue().some,
        category = item.category.name.some
      )
      val received: ItemProto = item.map[ItemProto]
      assertEquals(received, expected)
    }).fold(fail("This test shouldn't fail"))(r => r)
  }

  test("An invalid ItemProto can't be converted")(
    invalidItemProtoGen.sample.fold(fail("Error creating IdProto instance"))(itemProto => {

      val expected = Invalid(
        NonEmptyList.of(
          MissingField.of("Item Id"),
          MissingField.of("Item name"),
          MissingField.of("Item description"),
          MissingField.of("Item price"),
          MissingField.of("Item category")
        ))
      val received = itemProto.mapV[Item]
      assert(received.isInvalid, "ItemProto should be invalid")
      assertEquals(expected, received)
    })
  )
}
