package co.bbt.ref.domain

import co.bbt.ref.ErrorMessage

sealed abstract class DomainError(val error: ErrorMessage) extends Exception(error.message)

sealed abstract class ValidationError(override val error: ErrorMessage) extends DomainError(error)

sealed abstract class InvalidInput(override val error: ErrorMessage) extends DomainError(error)

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
