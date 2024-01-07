package domain.table

case class TableName(private val value: String) extends AnyVal {
  def toSqlSentence: String = value
}
