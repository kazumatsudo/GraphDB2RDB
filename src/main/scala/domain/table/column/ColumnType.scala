package domain.table.column

sealed trait ColumnType
case object ColumnTypeBoolean extends ColumnType
case class ColumnTypeInt(private val length: ColumnLength) extends ColumnType
case object ColumnTypeDouble extends ColumnType
case class ColumnTypeString(private val length: ColumnLength) extends ColumnType
case object ColumnTypeUnknown extends ColumnType