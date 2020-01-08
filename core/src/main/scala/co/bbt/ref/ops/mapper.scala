package co.bbt.ref.ops

import cats.data.ValidatedNel
import co.bbt.ref.domain.DomainError

trait Transformer[-A, +B] {
  def tr(a: A): B
}

object Transformer {
  def apply[A, B](implicit tr: Transformer[A, B]): A Transformer B = tr
}

sealed trait TransformerSyntax {
  implicit final def transformToOps[A](a: A): TransformToOps[A] = new TransformToOps[A](a)
}

final class TransformToOps[A](private val a: A) extends AnyVal {
  type V[B] = ValidatedNel[DomainError, B]

  def map[B: Transformer[A, *]]: B                    = Transformer[A, B].tr(a)
  def mapV[B](implicit m: Transformer[A, V[B]]): V[B] = Transformer[A, V[B]].tr(a)
}

object mapper extends TransformerSyntax
