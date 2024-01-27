package domain.table.ddl.key

import domain.table.ddl.column.ColumnName

case class PrimaryKey(private val value: Set[ColumnName]) extends AnyVal {

  /** merges PrimaryKey in two primary key list into one
    *
    * @param target
    *   target PrimaryKey
    * @return
    *   merged primary key list
    */
  def merge(target: PrimaryKey): PrimaryKey = PrimaryKey(value ++ target.value)

  def toSqlSentence: String =
    s"PRIMARY KEY (${value.toSeq.sortBy(_.toSqlSentence).map(_.toSqlSentence).mkString(", ")})"
}
