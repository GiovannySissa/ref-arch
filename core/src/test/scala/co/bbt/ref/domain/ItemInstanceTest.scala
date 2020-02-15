package co.bbt.ref.domain

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import co.bbt.ref.domain.item.Item
import co.bbt.ref.generators.CoreGenerators
import minitest.SimpleTestSuite

object ItemInstanceTest extends SimpleTestSuite with CoreGenerators {
  test("Create an Item successful") {
    itemGenerator.sample.fold(fail("Error creating item"))(validItem =>
      validItem.fold(
        _ => fail("Item isn't valid"),
        item => assertEquals(validItem, Valid(Item(item.id, item.name, item.description, item.price, item.category)))))
  }

  test("An item cannot be created with invalid params") {
    invalidItemGenerator.sample.fold(fail("Fail creating invalid item"))(invalid =>
      assertEquals(
        invalid,
        Invalid(
          NonEmptyList
            .of(InvalidItemId(), InvalidName(), EmptyDescription(), NegativePrice(), InvalidItemCategory()))))
  }
}
