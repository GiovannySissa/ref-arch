package co.bbt.ref.program.interpreters

import cats.effect.IO
import cats.instances.list._
import cats.syntax.traverse._
import co.bbt.ref.generators.CoreGenerators

object LiveItemPostgresRepositoryITTest extends BaseRepositorySuite with CoreGenerators {
  test("Find all items successful") { repoResource =>
    itemsGenerator.sample
      .fold(fail("Generating item to use"))(
        itemValid =>
          itemValid.fold(
            _ => fail("Invalid item couldn't create"),
            items =>
              repoResource
                .use(repo =>
                  for {
                    _     <- items.traverse(repo.itemRepository.create)
                    found <- repo.itemRepository.findAll.getOrElseF(IO.pure(List.empty))
                  } yield assertEquals(found, items))
                .unsafeRunSync()
          ))
  }

  test("Create an item successful") { repoResource =>
    itemGenerator.sample
      .fold(fail("Generating item to use"))(
        itemValid =>
          itemValid.fold(
            _ => fail("Invalid item couldn't create"),
            item =>
              repoResource
                .use(repo => repo.itemRepository.create(item).map(received => assertEquals(received, item)))
                .unsafeRunSync()
          ))
  }

  test("Update an item successful") { repoResource =>
    itemGenerator.sample
      .fold(fail("Generating item to use"))(
        itemValid =>
          itemValid.fold(
            _ => fail("Invalid item couldn't create"),
            item =>
              repoResource
                .use(repo =>
                  for {
                    created <- repo.itemRepository.create(item)
                    nameUpdated = created.name.copy(value = "Name updated")
                    updated <- repo.itemRepository.update(created.copy(name = nameUpdated))
                  } yield assertEquals(updated, item.copy(name = nameUpdated)))
                .unsafeRunSync()
          ))
  }

  test("Find an item successful") { repoResource =>
    itemGenerator.sample
      .fold(fail("Generating item to use"))(
        itemValid =>
          itemValid.fold(
            _ => fail("Invalid item couldn't create"),
            item =>
              repoResource
                .use(repo =>
                  for {
                    created <- repo.itemRepository.create(item)
                    found   <- repo.itemRepository.find(created.id).getOrElseF(IO.pure(fail("item not found")))
                  } yield assertEquals(found, item))
                .unsafeRunSync()
          ))
  }

  test("Delete an item successful") { repoResource =>
    itemGenerator.sample
      .fold(fail("Generating item to use"))(
        itemValid =>
          itemValid.fold(
            _ => fail("Invalid item couldn't create"),
            item =>
              repoResource
                .use(repo =>
                  for {
                    created <- repo.itemRepository.create(item)
                    deleted <- repo.itemRepository.delete(created.id)
                  } yield assertEquals(deleted, ()))
                .unsafeRunSync()
          ))
  }

  test("Cannot get an item not created") { repoResource =>
    itemGenerator.sample
      .fold(fail("Generating item to use"))(
        itemValid =>
          itemValid.fold(
            _ => fail("Invalid item couldn't create"),
            item =>
              repoResource
                .use(repo => {
                  repo.itemRepository.find(item.id).value.map(maybe => assertEquals(maybe, None))
                })
                .unsafeRunSync()
          ))
  }
}
