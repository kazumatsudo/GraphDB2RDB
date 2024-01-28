package domain.table.ddl

import domain.table.ddl.attribute.{ForeignKey, PrimaryKey, UniqueIndex}

import scala.collection.View

final case class TableAttributes(
    private val primaryKey: PrimaryKey,
    private val foreignKey: ForeignKey,
    private val uniqueIndex: UniqueIndex
) {

  /** merges TableAttribute in two table attributes into one
    *
    * @param target
    *   target TableAttribute
    * @param checkUnique
    *   whether each value in the TableAttribute is unique.
    * @return
    *   merged TableAttribute
    */
  def merge(target: TableAttributes, checkUnique: Boolean): TableAttributes =
    TableAttributes(
      primaryKey.merge(target.primaryKey),
      foreignKey.merge(target.foreignKey, checkUnique),
      uniqueIndex.merge(target.uniqueIndex, checkUnique)
    )

  def toSqlSentenceView: View[String] =
    Seq(
      primaryKey.toSqlSentence
    ).view ++ foreignKey.toSqlSentenceView ++ uniqueIndex.toSqlSentenceView
}
