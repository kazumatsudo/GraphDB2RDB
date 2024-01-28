package domain.table.ddl

import domain.table.ddl.attribute.{PrimaryKey, UniqueIndex}

final case class TableAttributes(
    private val primaryKey: PrimaryKey,
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
      uniqueIndex.merge(target.uniqueIndex, checkUnique)
    )

  def toSqlSentenceSeq: Seq[String] =
    primaryKey.toSqlSentence +: uniqueIndex.toSqlSentenceSeq
}
