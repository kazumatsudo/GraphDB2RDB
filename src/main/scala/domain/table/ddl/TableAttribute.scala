package domain.table.ddl

final case class TableAttribute(private val foreignKey: ForeignKey) {

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
    TableAttribute(foreignKey.merge(target.foreignKey, checkUnique))

  def toSqlSentence: Seq[String] = foreignKey.toSqlSentence
}
