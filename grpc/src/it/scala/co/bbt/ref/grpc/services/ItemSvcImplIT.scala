package co.bbt.ref.grpc.services

import cats.instances.list._
import cats.syntax.option._
import cats.syntax.traverse._
import co.bbt.ref.proto.Item.{ItemDeleteMsgProto, ItemIdProto, ItemsProto}
import com.google.protobuf.empty.Empty
import io.grpc.Metadata

import scala.concurrent.Future

object ItemenvIT extends ItBaseGrpcTestSuite {

  testAsync("Create an ITem success") { resource =>
    Future.successful(
      itemProtoGen.sample.fold(fail("Error in ItemProto generator"))(proto =>
        resource
          .use(
            _.client
              .createItem(proto, new Metadata)
              .map(received => assertEquals(received, proto)))
          .unsafeRunSync))
  }

  testAsync("Update an item successful") { resource =>
    Future.successful(itemProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {
      val expected = itemProto.copy(name = "Name Updated".some)
      resource
        .use(env =>
          for {
            created <- env.client.createItem(itemProto, new Metadata)
            updated <- env.client updateItem (created.copy(name = "Name Updated".some), new Metadata)
          } yield {
            assertEquals(updated, expected)
          })
        .unsafeRunSync
    }))
  }

  testAsync("Find an item successful") { resource =>
    Future.successful(
      itemProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {
        resource
          .use(env =>
            for {
              created <- env.client.createItem(itemProto, new Metadata)
              found   <- env.client.findItem(ItemIdProto(id = created.id), new Metadata)
            } yield {
              assertEquals(found, itemProto)
            })
          .unsafeRunSync
      })
    )
  }

  testAsync("FindAll items successful") { resource =>
    Future.successful(
      itemsProtoGen.sample.fold(fail("Fail creating a list of item protos"))(itemProtos => {
        val expected: ItemsProto = ItemsProto(items = itemProtos.sortBy(_.price.getOrElse(Long.MinValue)))
        resource
          .use(env =>
            for {
              _        <- itemProtos.traverse(env.client.createItem(_, new Metadata))
              received <- env.client.findAll(new Empty, new Metadata)
            } yield {
              assert(received.items.nonEmpty, "List got must have elements")
              assertEquals(received.items.sortBy(_.price.getOrElse(Long.MinValue)), expected.items)
            })
          .unsafeRunSync
      })
    )
  }

  testAsync("Delete an item successful") { resource =>
    Future.successful(
      itemProtoGen.sample.fold(fail("Fail creating an item proto"))(itemProto => {
        resource
          .use(env =>
            for {
              created <- env.client.createItem(itemProto, new Metadata)
              deleted <- env.client.deleteItem(ItemIdProto(id = created.id), new Metadata)
            } yield {
              assertEquals(deleted, ItemDeleteMsgProto(message = "Item deleted!".some))
            })
          .unsafeRunSync
      })
    )
  }

}
