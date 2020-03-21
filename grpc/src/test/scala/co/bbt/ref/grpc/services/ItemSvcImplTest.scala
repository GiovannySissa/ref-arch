package co.bbt.ref.grpc.services

import cats.instances.list._
import cats.syntax.option._
import cats.syntax.traverse._
import co.bbt.ref.domain.item.{Item, ItemID}
import co.bbt.ref.domain.{DomainError, ErrorContainer, ItemAlreadyExist, ItemNotFound, MissingField}
import co.bbt.ref.ops.mapper._
import co.bbt.ref.proto.Item.{ItemDeleteMsgProto, ItemIdProto, ItemsProto}
import com.google.protobuf.empty.Empty
import io.grpc.Metadata

object ItemSvcImplTest extends BaseGrpcTestSuite {

  test("Create an item using grpc services implementation successful") { resource =>
    itemProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto =>
      resource
        .use(svcImpl => {
          svcImpl
            .createItem(itemProto, new Metadata)
            .map(received => {
              assertEquals(received, itemProto)
              ()
            })
        })
        .unsafeRunSync)
  }

  test("Update an item using grpc services implementation successful") { resource =>
    itemProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {
      val expected = itemProto.copy(name = "Name Updated".some)
      resource
        .use(svcImpl => {

          for {
            created <- svcImpl.createItem(itemProto, new Metadata)
            updated <- svcImpl.updateItem(created.copy(name = "Name Updated".some), new Metadata)
          } yield {
            assertEquals(updated, expected)
          }
        })
        .unsafeRunSync
    })
  }

  test("Find an item using grpc services implementation successful") { resource =>
    itemProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {

      resource
        .use(svcImpl => {

          for {
            created <- svcImpl.createItem(itemProto, new Metadata)
            found   <- svcImpl.findItem(ItemIdProto(id = created.id), new Metadata)
          } yield {
            assertEquals(found, itemProto)
          }
        })
        .unsafeRunSync
    })
  }

  test("FindAll items using grpc services implementation successful") { resource =>
    itemsProtoGen.sample.fold(fail("Fail creating a list of item protos"))(itemProtos => {
      val expected: ItemsProto = ItemsProto(items = itemProtos.sortBy(_.price.getOrElse(Long.MinValue)))
      resource
        .use(svcImpl =>
          for {
            _        <- itemProtos.traverse(svcImpl.createItem(_, new Metadata))
            received <- svcImpl.findAll(new Empty(), new Metadata())
          } yield {
            assert(received.items.nonEmpty, "List got must have elements")
            assertEquals(received.items.sortBy(_.price.getOrElse(Long.MinValue)), expected.items)
          })
        .unsafeRunSync
    })

  }

  test("Delete an item using grpc services implementation successful") { resource =>
    itemProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {

      resource
        .use(svcImpl => {

          for {
            created <- svcImpl.createItem(itemProto, new Metadata)
            deleted <- svcImpl.deleteItem(ItemIdProto(id = created.id), new Metadata)
          } yield {
            assertEquals(deleted, ItemDeleteMsgProto(message = "Item deleted!".some))
          }
        })
        .unsafeRunSync
    })
  }

  test("Can't create an item already exists") { resource =>
    itemProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {
      val expected = itemProto.mapV[Item].toEither.map(item => ItemAlreadyExist.of(item.id)).swap

      resource
        .use(svcImpl => {

          for {
            created  <- svcImpl.createItem(itemProto, new Metadata)
            received <- svcImpl.createItem(created, new Metadata).attempt
          } yield {
            assert(received.isLeft, "Couldn't create an existing item")

            assertEquals(received, expected)
          }
        })
        .unsafeRunSync
    })

  }

  test("Can't create an item with a invalid input") { resource =>
    invalidItemProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {

      resource
        .use(svcImpl => {

          svcImpl
            .createItem(itemProto, new Metadata)
            .attempt
            .map(received => {
              assert(received.isLeft, "Shouldn't create an item with invalid input")
              assertEquals(received, errorsExpected)
            })
        })
        .unsafeRunSync
    })
  }

  test("Can't update an item with a invalid input") { resource =>
    invalidItemProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {

      resource
        .use(svcImpl => {

          svcImpl
            .updateItem(itemProto, new Metadata)
            .attempt
            .map(received => {
              assert(received.isLeft, "Shouldn't update an item with invalid input")
              assertEquals(received, errorsExpected)
            })
        })
        .unsafeRunSync
    })
  }

  test("Can't find an item with a invalid input") { resource =>
    invalidIdProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {

      resource
        .use(svcImpl => {

          svcImpl
            .findItem(itemProto, new Metadata)
            .attempt
            .map(received => {
              assert(received.isLeft, "Shouldn't get an item with invalid input")
              assertEquals(received, errorExpected)
            })
        })
        .unsafeRunSync
    })
  }

  test("Can't delete an item with a invalid input") { resource =>
    invalidIdProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {

      resource
        .use(svcImpl => {

          svcImpl
            .deleteItem(itemProto, new Metadata)
            .attempt
            .map(received => {
              assert(received.isLeft, "Shouldn't get an item with invalid input")
              assertEquals(received, errorExpected)
            })
        })
        .unsafeRunSync
    })
  }

  test("Can't update a non-existent item") { resource =>
    itemProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {
      val expected = itemProto.mapV[Item].toEither.map(item => ItemNotFound.of(item.id)).swap

      resource
        .use(svcImpl => {

          svcImpl
            .updateItem(itemProto, new Metadata)
            .attempt
            .map(received => {
              assert(received.isLeft, "Shouldn't update a non-existing item")
              assertEquals(received, expected)
            })
        })
        .unsafeRunSync
    })
  }

  test("Can't get a non-existent item") { resource =>
    idProtoGen.sample.fold(fail("Fail creating an item proto"))(idProto => {
      val expected = idProto.mapV[ItemID].toEither.map(ItemNotFound.of(_)).swap

      resource
        .use(svcImpl => {

          svcImpl
            .findItem(idProto, new Metadata)
            .attempt
            .map(received => {
              assert(received.isLeft, "Shouldn't get an non-existing item")
              assertEquals(received, expected)
            })
        })
        .unsafeRunSync
    })
  }

  test("Can't delete a non-existent item") { resource =>
    idProtoGen.sample.fold(fail("Fail creating an item proto"))(idProto => {
      val expected = idProto.mapV[ItemID].toEither.map(ItemNotFound.of(_)).swap

      resource
        .use(svcImpl => {

          svcImpl
            .deleteItem(idProto, new Metadata)
            .attempt
            .map(received => {
              assert(received.isLeft, "Shouldn't delete an non-existing item")
              assertEquals(received, expected)
            })
        })
        .unsafeRunSync
    })
  }

  private val errorsExpected: Left[ErrorContainer, Nothing] = Left(
    ErrorContainer(
      List[DomainError](
        MissingField.of("Item Id"),
        MissingField.of("Item name"),
        MissingField.of("Item description"),
        MissingField.of("Item price"),
        MissingField.of("Item category")
      )))

  private val errorExpected: Left[ErrorContainer, Nothing] = Left(
    ErrorContainer(List[DomainError](MissingField.of("Item Id"))))

}
