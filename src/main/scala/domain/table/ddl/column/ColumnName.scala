package domain.table.ddl.column

case class ColumnName(private val value: String) extends AnyVal {
  def toSqlSentence: String = value
}
