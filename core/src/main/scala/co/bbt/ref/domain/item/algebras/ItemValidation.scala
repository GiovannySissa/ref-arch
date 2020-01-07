package co.bbt.ref.domain.item.algebras

import co.bbt.ref.domain.item.ItemID

trait ItemValidation[F[_]] {
  def exist(itemID: ItemID): F[Unit]
  def notExist(itemID: ItemID): F[Unit]
}
object ItemValidation {
  def apply[F[_]: ItemValidation]: ItemValidation[F] = implicitly
}
