package co.bbt.ref.domain

import co.bbt.ref.ErrorMessage
import co.bbt.ref.domain.item.ItemID

sealed abstract class DomainError(val error: ErrorMessage) extends Exception(error.message)

sealed abstract class ValidationError(override val error: ErrorMessage) extends DomainError(error)

sealed abstract class InvalidInput(override val error: ErrorMessage) extends DomainError(error)

final case class ErrorContainer(private val errors: List[DomainError])
  extends DomainError(error = ErrorMessage(errors.map(_.error.message).mkString(", ")))

final case class InvalidItemId(override val error: ErrorMessage = ErrorMessage("Invalid Item Id"))
  extends InvalidInput(error)
final case class InvalidName(override val error: ErrorMessage = ErrorMessage("Invalid Item name"))
  extends InvalidInput(error)

final case class EmptySerialID(
  override val error: ErrorMessage = ErrorMessage("Empty Serial ID")
) extends InvalidInput(error)

final case class UnknownID(
  override val error: ErrorMessage = ErrorMessage("Unknown ID type")
) extends InvalidInput(error)

final case class EmptyDescription(override val error: ErrorMessage = ErrorMessage("Empty description"))
  extends InvalidInput(error)
final case class NegativePrice(override val error: ErrorMessage = ErrorMessage("Invalid price value"))
  extends InvalidInput(error)
final case class InvalidItemCategory(override val error: ErrorMessage = ErrorMessage("Invalid item category"))
  extends InvalidInput(error)

final case class ItemAlreadyExist(id: ItemID)(
  override val error: ErrorMessage = ErrorMessage(s"Item with id ${id.value} already exist"))
  extends ValidationError(error)

object ItemAlreadyExist {
  def of(id: ItemID): ItemAlreadyExist = ItemAlreadyExist(id)()
}

final case class ItemNotFound(id: ItemID)(
  override val error: ErrorMessage = ErrorMessage(s"Item with id ${id.value} not found"))
  extends ValidationError(error)

object ItemNotFound {
  def of(id: ItemID): ItemNotFound = ItemNotFound(id)()
}

final case class MissingField(name: String)(
  override val error: ErrorMessage = ErrorMessage(s"The field $name is required "))
  extends InvalidInput(error)

object MissingField {
  def of(name: String): MissingField = MissingField(name)()
}
