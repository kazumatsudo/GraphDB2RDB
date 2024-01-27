package domain.table.ddl

import domain.table.ddl.key.{ForeignKey, PrimaryKey}

final case class TableAttribute(
    private val primaryKey: PrimaryKey,
    private val foreignKey: ForeignKey
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
      foreignKey.merge(target.foreignKey, checkUnique)
    )

  def toSqlSentence: Seq[String] = {
    primaryKey.toSqlSentence +: foreignKey.toSqlSentence
  }
}
