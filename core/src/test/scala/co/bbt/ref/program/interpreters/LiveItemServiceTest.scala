package co.bbt.ref.program.interpreters

import cats.effect.{IO, Sync}
import cats.instances.list._
import cats.instances.tuple._
import cats.syntax.either._
import cats.syntax.functor._
import cats.syntax.traverse._
import co.bbt.ref.domain.ItemNotFound
import co.bbt.ref.domain.item.Item
import co.bbt.ref.generators.CoreGenerators
import co.bbt.ref.infrastructure.persistence.memory.ItemInMemoryRepository
import co.bbt.ref.program.modules.{LiveRepository, LiveService, LiveValidation}
import com.olegpy.meow.hierarchy._
import minitest.TestSuite

object LiveItemServiceTest extends TestSuite[(LiveService[IO], ItemInMemoryRepository[IO])] with CoreGenerators {
  test("Create an item successful") { resources =>
    val (svc, _) = resources
    itemGenerator.sample.fold(fail("Generating an Item"))(valid =>
      valid.fold(
        _ => fail("Invalid Item"),
        item =>
          svc.itemService
            .createItem(item)
            .map(received => assertEquals(received, item))
            .unsafeRunSync))
  }

  test("Update an item successful") { resources =>
    val (svc, _) = resources
    itemGenerator.sample.fold(fail("Generating an Item"))(valid =>
      valid.fold(
        _ => fail("Invalid Item"),
        item =>
          (for {
            created <- svc.itemService.createItem(item)
            newName = created.name.copy(value = "new name test")
            updated <- svc.itemService.updateItem(item.copy(name = newName))
          } yield assertEquals(updated, item.copy(name = newName))).unsafeRunSync
      ))
  }

  test("Find an item successful") { resources =>
    val (svc, _) = resources
    itemGenerator.sample.fold(fail("Generating an Item"))(valid =>
      valid.fold(
        _ => fail("Invalid Item"),
        item =>
          (for {
            created <- svc.itemService.createItem(item)
            found   <- svc.itemService.findItem(created.id)
          } yield assertEquals(found, item)).unsafeRunSync
      ))
  }

  test("Find a list of item successful") { resources =>
    val (svc, _) = resources
    itemsGenerator.sample.fold(fail("Generating a list of Item"))(valid =>
      valid.fold(
        _ => fail("Invalid Items"),
        items =>
          (for {
            _     <- items.traverse(svc.itemService.createItem)
            found <- svc.itemService.findAllItems
          } yield assertEquals(found, items)).unsafeRunSync
      ))
  }

  test("Delete an item successful") { resources =>
    val (svc, _) = resources
    itemGenerator.sample.fold(fail("Generating an Item"))(valid =>
      valid.fold(
        _ => fail("Invalid Item"),
        item =>
          (for {
            created <- svc.itemService.createItem(item)
            deleted <- svc.itemService.deleteItem(created.id)
          } yield assertEquals(deleted, ())).unsafeRunSync
      ))
  }

  test("Get an empty list of item if they haven't created yet") { resources =>
    val (svc, _) = resources
    svc.itemService.findAllItems
      .map(received => assertEquals(received, List.empty[Item]))
      .unsafeRunSync
  }

  test("Get an error trying to get an item not created") { resources =>
    val (svc, _) = resources
    itemGenerator.sample.fold(fail("Generating an Item"))(valid =>
      valid.fold(
        _ => fail("Invalid Item"),
        item =>
          svc.itemService
            .findItem(item.id)
            .attempt
            .map { attempt =>
              attempt.leftMap(err => assertEquals(err, ItemNotFound.of(item.id)))
              assert(attempt.isLeft)
            }
            .unsafeRunSync()
      ))
  }

  override def setup(): (LiveService[IO], ItemInMemoryRepository[IO]) =
    createTestResources[IO].unsafeRunSync

  override def tearDown(env: (LiveService[IO], ItemInMemoryRepository[IO])): Unit = {
    val _ = env.map(_ => ())
  }
  private def createTestResources[F[_]: Sync]: F[(LiveService[F], ItemInMemoryRepository[F])] =
    ItemInMemoryRepository
      .makeRef[F]
      .map(ref => {
        val repo: ItemInMemoryRepository[F]   = ItemInMemoryRepository[F](ref)
        val liveRepo: LiveRepository[F]       = LiveRepository[F](repo)
        val liveValidation: LiveValidation[F] = LiveValidation[F](liveRepo)
        (LiveService[F](liveRepo, liveValidation), repo)
      })
}
