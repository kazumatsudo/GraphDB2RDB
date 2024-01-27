package domain.table.ddl

import domain.table.ddl.attribute.PrimaryKey

final case class TableAttributes(private val primaryKey: PrimaryKey) {

  /** merges TableAttribute in two table attributes into one
    *
    * @param target
    *   target TableAttribute
    * @return
    *   merged TableAttribute
    */
  def merge(target: TableAttributes): TableAttributes = TableAttributes(
    primaryKey.merge(target.primaryKey)
  )

  def toSqlSentenceSeq: Seq[String] = Seq(primaryKey.toSqlSentence)
}
