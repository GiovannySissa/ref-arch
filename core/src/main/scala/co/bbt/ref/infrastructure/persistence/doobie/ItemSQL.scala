package co.bbt.ref.infrastructure.persistence.doobie

import co.bbt.ref.infrastructure.persistence.rows.ItemRow
import doobie.implicits._

object ItemSQL {
  def select(id: String): doobie.Query0[ItemRow] =
    sql"""
      SELECT ID, NAME, DESCRIPTION, PRICE, CATEGORY
        FROM ITEM WHERE ID=$id""".queryWithLogHandler[ItemRow](LoggerHandler.handler)

  def selectAll: doobie.ConnectionIO[List[ItemRow]] =
    sql"""
      SELECT ID, NAME, DESCRIPTION, PRICE, CATEGORY
        FROM ITEM""".queryWithLogHandler[ItemRow](LoggerHandler.handler).to[List]

  def insert(itemRow: ItemRow): doobie.Update0 =
    sql"""
      INSERT INTO ITEM(ID, NAME, DESCRIPTION, PRICE, CATEGORY)
        VALUES(${itemRow.id}, ${itemRow.name}, ${itemRow.description}, ${itemRow.price}, ${itemRow.category})
      """.updateWithLogHandler(LoggerHandler.handler)

  def update(itemRow: ItemRow): doobie.Update0 =
    sql"""
      UPDATE ITEM SET ID = ${itemRow.id}, NAME = ${itemRow.name}, DESCRIPTION = ${itemRow.description}, PRICE = ${itemRow.price}, CATEGORY = ${itemRow.category}
        WHERE ID = ${itemRow.id}
      """.updateWithLogHandler(LoggerHandler.handler)

  def delete(id: String): doobie.Update0 =
    sql"""
      DELETE FROM ITEM WHERE ID = ${id}
      """.updateWithLogHandler(LoggerHandler.handler)
}
