package co.bbt.ref.program.interpreters

import cats.effect.{IO, Sync}
import cats.instances.tuple._
import cats.syntax.either._
import cats.syntax.functor._
import co.bbt.ref.domain.{ItemAlreadyExist, ItemNotFound}
import co.bbt.ref.generators.CoreGenerators
import co.bbt.ref.infrastructure.persistence.memory.ItemInMemoryRepository
import co.bbt.ref.program.modules.{LiveRepository, LiveValidation}
import com.olegpy.meow.hierarchy._
import minitest.TestSuite

object LiveValidationTest extends TestSuite[(LiveValidation[IO], ItemInMemoryRepository[IO])] with CoreGenerators {
  test("Validate when an item not exist successful") { validationResources =>
    val (liveValidation, _) = validationResources
    itemIDGenerator.sample.fold(fail("Creating item id "))(maybe =>
      maybe.fold(
        _ => fail("Invalid ItemID"),
        id => liveValidation.itemValidation.notExist(id).map(v => assertEquals(v, ())).unsafeRunSync
      ))
  }

  test("If an item exist handle validation error") { validationResources =>
    val (validationInterpreter, repo) = validationResources
    itemGenerator.sample.fold(fail("Creating item"))(maybe =>
      maybe.fold(
        _ => fail("Invalid Item"),
        item =>
          (for {
            created  <- repo.create(item)
            notExist <- validationInterpreter.itemValidation.notExist(created.id).attempt
          } yield {
            notExist.leftMap(err => assertEquals(err, ItemAlreadyExist.of(item.id)))
            assert(notExist.isLeft)
          }).unsafeRunSync()
      ))
  }

  test("If an item wasn't found handle validation error") { validationResources =>
    val (liveValidation, _) = validationResources
    itemIDGenerator.sample.fold(fail("Creating item id "))(maybe =>
      maybe.fold(
        _ => fail("Invalid ItemID"),
        id =>
          liveValidation.itemValidation
            .exist(id)
            .attempt
            .map { attempt =>
              attempt.leftMap(err => assertEquals(err, ItemNotFound.of(id)))
              assert(attempt.isLeft)
            }
            .unsafeRunSync
      ))
  }

  test("Don't handle error if an item exist") { validationResources =>
    val (validationInterpreter, repo) = validationResources
    itemGenerator.sample.fold(fail("Creating item"))(maybe =>
      maybe.fold(
        _ => fail("Invalid Item"),
        item =>
          (for {
            created <- repo.create(item)
            exists  <- validationInterpreter.itemValidation.exist(created.id)
          } yield assertEquals(exists, ())).unsafeRunSync()
      ))
  }

  private def createValidationInterpreter[F[_]: Sync]: F[(LiveValidation[F], ItemInMemoryRepository[F])] =
    ItemInMemoryRepository
      .makeRef[F]
      .map(ref => {
        val repo: ItemInMemoryRepository[F] = ItemInMemoryRepository[F](ref)
        (LiveValidation[F](LiveRepository[F](repo)), repo)
      })

  override def setup(): (LiveValidation[IO], ItemInMemoryRepository[IO]) =
    createValidationInterpreter[IO].unsafeRunSync()

  override def tearDown(env: (LiveValidation[IO], ItemInMemoryRepository[IO])): Unit = {
    val _ = env.map(_ => ())
  }
}
