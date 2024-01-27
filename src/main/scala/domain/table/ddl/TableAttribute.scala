package domain.table.ddl

import domain.table.ddl.attribute.{ForeignKey, UniqueIndex, PrimaryKey}

final case class TableAttribute(
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
  def merge(target: TableAttribute, checkUnique: Boolean): TableAttribute =
    TableAttribute(
      primaryKey.merge(target.primaryKey),
      foreignKey.merge(target.foreignKey, checkUnique),
      uniqueIndex.merge(target.uniqueIndex, checkUnique)
    )

  def toSqlSentence: Seq[String] = {
    primaryKey.toSqlSentence +: (foreignKey.toSqlSentence ++ uniqueIndex.toSqlSentence)
  }
}
