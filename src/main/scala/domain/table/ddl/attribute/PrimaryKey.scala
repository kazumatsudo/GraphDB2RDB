package domain.table.ddl.attribute

import domain.table.ddl.column.ColumnName

final case class PrimaryKey(private val value: Set[ColumnName]) extends AnyVal {

  /** merges PrimaryKey in two primary key list into one
    *
    * @param target
    *   target PrimaryKey
    * @return
    *   merged primary key list
    */
  def merge(target: PrimaryKey): PrimaryKey = if (value == target.value) {
    this
  } else {
    throw new IllegalArgumentException(
      s"primary key must be unique. detected values: $value and ${target.value}"
    )
  }

  def toSqlSentence: String =
    s"PRIMARY KEY (${value.toSeq.sortBy(_.toSqlSentence).map(_.toSqlSentence).mkString(", ")})"
}
