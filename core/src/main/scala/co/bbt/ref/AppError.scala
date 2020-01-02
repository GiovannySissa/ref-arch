package co.bbt.ref

abstract class AppError(val error: ErrorMessage) extends Exception(error.message)

final case class AppFailure(
  override val error: ErrorMessage = ErrorMessage("Something went wrong!")
) extends AppError(error)

final case class ErrorMessage private (message: String) extends AnyVal
