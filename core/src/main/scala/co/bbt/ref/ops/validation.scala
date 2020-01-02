package co.bbt.ref.ops

import cats.ApplicativeError
import cats.syntax.applicative._
import mouse.boolean._

sealed trait ValidationSyntax {
  implicit final def validationOps[O](a: O): ValidationOps[O] =
    new ValidationOps(a)
}

final class ValidationOps[O](private val a: O) extends AnyVal {
  def condF[F[_]: ApplicativeError[*[_], E], E](f: O => Boolean)(e: E): F[O] =
    f(a).fold(
      a.pure[F],
      ApplicativeError[F, E].raiseError(e)
    )
}

object validation extends ValidationSyntax
