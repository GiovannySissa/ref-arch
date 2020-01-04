package co.bbt.ref.generators

import cats.data.ValidatedNel
import cats.instances.list._
import cats.syntax.traverse._
import co.bbt.ref.domain.InvalidInput
import co.bbt.ref.domain.item.Item
import co.bbt.ref.infrastructure.persistence.rows.ItemRow
import org.scalacheck.Gen

trait CoreGenerators {
  val nameGen: Gen[String]        = Gen.listOfN(10, Gen.alphaChar).map(_.mkString(""));
  val idGen: Gen[String]          = Gen.uuid.map(_.toString)
  val descriptionGen: Gen[String] = Gen.listOfN(20, Gen.alphaChar).map(_.mkString(""));
  val priceGen: Gen[BigDecimal]   = Gen.choose(0, 3000).map(BigDecimal(_));
  val categoryGen: Gen[String]    = Gen.listOfN(12, Gen.alphaChar).map(_.mkString(""));

  def itemGenerator: Gen[ValidatedNel[InvalidInput, Item]] =
    for {
      name        <- nameGen
      id          <- idGen
      description <- descriptionGen
      price       <- priceGen
      category    <- categoryGen
    } yield Item(id = id, name = name, description = description, price = price, category = category)

  def itemsGenerator: Gen[ValidatedNel[InvalidInput, List[Item]]] = Gen.listOfN(3, itemGenerator).map(_.sequence)

  def invalidItemGenerator: Gen[ValidatedNel[InvalidInput, Item]] = Gen.const(Item("", "", "", -1, ""))

  def itemRowGenerator: Gen[ItemRow] =
    for {
      name        <- nameGen
      id          <- idGen
      description <- descriptionGen
      price       <- priceGen
      category    <- categoryGen
    } yield ItemRow(id = id, name = name, description = description, price = price, category = category)
}
