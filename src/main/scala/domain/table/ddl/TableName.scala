package domain.table.ddl

case class TableName(private val value: String) extends AnyVal {
  private def maxLength = 64

  def toSqlSentence: String = if (value.length > maxLength) {
    value.substring(0, maxLength)
  } else
    value
}
