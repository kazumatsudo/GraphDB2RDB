package domain.table.column

sealed trait ColumnType {

  /** merges the attributes (type, length...) in two columns into one
   *
   * @param columnType target column
   * @return merged column attributes
   */
  def merge(columnType: ColumnType): ColumnType
}

case object ColumnTypeBoolean extends ColumnType {
  override def merge(columnType: ColumnType): ColumnType = columnType match {
    case ColumnTypeBoolean => ColumnTypeBoolean
    case ColumnTypeInt(length) => ColumnTypeInt(length.max(5)) // 5 = false.toString
    case ColumnTypeDouble(length) => ColumnTypeDouble(length.max(5)) // 5 = false.toString
    case ColumnTypeString(length) => ColumnTypeString(length.max(5)) // 5 = false.toString
    case ColumnTypeUnknown => ColumnTypeUnknown
  }
}

case class ColumnTypeInt(private val length: ColumnLength) extends ColumnType {
  override def merge(columnType: ColumnType): ColumnType = columnType match {
    case ColumnTypeBoolean => ColumnTypeBoolean.merge(ColumnTypeInt(length))
    case ColumnTypeInt(alength) => ColumnTypeInt(alength.max(length))
    case ColumnTypeDouble(alength) => ColumnTypeDouble(alength.max(length))
    case ColumnTypeString(alength) => ColumnTypeString(alength.max(length))
    case ColumnTypeUnknown => ColumnTypeUnknown
  }
}
case class ColumnTypeDouble(private val length: ColumnLength) extends ColumnType {
  override def merge(columnType: ColumnType): ColumnType = columnType match {
    case ColumnTypeBoolean => ColumnTypeBoolean.merge(ColumnTypeDouble(length))
    case ColumnTypeInt(alength) => ColumnTypeInt(alength).merge(ColumnTypeDouble(length))
    case ColumnTypeDouble(alength) => ColumnTypeDouble(alength.max(length))
    case ColumnTypeString(alength) => ColumnTypeString(alength.max(length))
    case ColumnTypeUnknown => ColumnTypeUnknown
  }
}

case class ColumnTypeString(private val length: ColumnLength) extends ColumnType {
  override def merge(columnType: ColumnType): ColumnType = columnType match {
    case ColumnTypeBoolean => ColumnTypeBoolean.merge(ColumnTypeString(length))
    case ColumnTypeInt(alength) => ColumnTypeInt(alength).merge(ColumnTypeString(length))
    case ColumnTypeDouble(alength) => ColumnTypeDouble(alength).merge(ColumnTypeString(length))
    case ColumnTypeString(alength) => ColumnTypeString(alength.max(length))
    case ColumnTypeUnknown => ColumnTypeUnknown
  }
}

case object ColumnTypeUnknown extends ColumnType {
  override def merge(columnType: ColumnType): ColumnType = columnType match {
    case ColumnTypeBoolean => ColumnTypeBoolean.merge(ColumnTypeUnknown)
    case ColumnTypeInt(length) => ColumnTypeInt(length).merge(ColumnTypeUnknown)
    case ColumnTypeDouble(length) => ColumnTypeDouble(length).merge(ColumnTypeUnknown)
    case ColumnTypeString(length) => ColumnTypeString(length).merge(ColumnTypeUnknown)
    case ColumnTypeUnknown => ColumnTypeUnknown
  }
}
