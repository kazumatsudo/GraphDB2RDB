package domain.table.ddl

case class TableName(private val value: String) extends AnyVal {
  def toSqlSentence: String = value
}
