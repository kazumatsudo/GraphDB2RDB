package domain.table.ddl.column

final case class ColumnName(private val value: String) extends AnyVal {
  def toSqlSentence: String = value
}
