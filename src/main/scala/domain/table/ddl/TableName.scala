package domain.table.ddl

case class TableName(private val value: String) extends AnyVal {

  // for foreign_key, substring 7 characters ('_ibfk_1')
  private def maxLength = 64 - 7

  def toSqlSentence: String = if (value.length > maxLength) {
    value.substring(0, maxLength)
  } else
    value
}
