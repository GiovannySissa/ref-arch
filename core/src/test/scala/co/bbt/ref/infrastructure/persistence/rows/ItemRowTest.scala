package co.bbt.ref.infrastructure.persistence.rows

import co.bbt.ref.domain.item.Item
import co.bbt.ref.generators.CoreGenerators
import minitest.SimpleTestSuite
import co.bbt.ref.ops.mapper._

object ItemRowTest extends SimpleTestSuite with CoreGenerators {
  test("Transform an ItemRow to Item successful") {
    itemRowGenerator.sample
      .fold(fail("Creating ItemRow"))(row =>
        assertEquals(
          row.mapV[Item],
          Item(
            id = row.id,
            name = row.name,
            description = row.description,
            price = row.price,
            category = row.category)))
  }

  test("Transform an Item to ItemRow successful") {
    itemGenerator.sample
      .fold(fail("Creating Item"))(itemValid =>
        itemValid.fold(
          _ => fail("Invalid item couldn't validate"),
          item =>
            assertEquals(
              item.map[ItemRow],
              ItemRow(
                id = item.id.value,
                name = item.name.value,
                description = item.description.value,
                price = item.price.value.amount,
                category = item.category.name
              )
            )
        ))
  }
}
