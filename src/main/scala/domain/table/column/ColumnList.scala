package domain.table.column

case class ColumnList(private val value: Map[ColumnName, ColumnType]) extends AnyVal
