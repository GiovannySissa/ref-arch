package co.bbt.ref.generators

import cats.data.ValidatedNel
import co.bbt.ref.domain.InvalidInput
import co.bbt.ref.domain.item.Item
import org.scalacheck.Gen

trait CoreGenerators {
  val nameGen: Gen[String] = Gen.listOfN(10, Gen.alphaChar).map(_.mkString(""));
  val idGen: Gen[String] = Gen.uuid.map(_.toString)

  def itemGenerator: Gen[ValidatedNel[InvalidInput, Item]] =
    for {
      name <- nameGen
      id   <- idGen
    } yield Item(id = id, name = name)

  def invalidItemGenerator: Gen[ValidatedNel[InvalidInput, Item]] = Gen.const(Item("", ""))
}
