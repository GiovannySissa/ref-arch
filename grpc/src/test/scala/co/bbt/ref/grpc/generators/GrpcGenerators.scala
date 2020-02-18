package co.bbt.ref.grpc.generators

import cats.syntax.option._
import co.bbt.ref.generators.CoreGenerators
import co.bbt.ref.proto.Item.{ItemIdProto, ItemProto}
import org.scalacheck.Gen

trait GrpcGenerators extends CoreGenerators {

  def protoPriceGen: Gen[Long]     = Gen.choose(0L, Long.MaxValue)
  def idProtoGen: Gen[ItemIdProto] = idGen.map(id => ItemIdProto(id.some))

  def itemProtoGen: Gen[ItemProto] =
    for {
      name        <- nameGen
      id          <- idGen
      description <- descriptionGen
      price       <- protoPriceGen
      category    <- categoryGen
    } yield ItemProto(
      id = id.some,
      name = name.some,
      description = description.some,
      price = price.some,
      category = category.some)

  def itemsProtoGen: Gen[List[ItemProto]] = Gen.listOf(itemProtoGen)
  def invalidItemProtoGen: Gen[ItemProto] = Gen.const(ItemProto(None, None, None, None, None))

  def invalidIdProtoGen: Gen[ItemIdProto] = Gen.const(ItemIdProto(None))
}
