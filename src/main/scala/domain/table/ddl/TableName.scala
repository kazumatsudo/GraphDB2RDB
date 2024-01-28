package domain.table.ddl

case class TableName(private val value: String) extends AnyVal {

  // in MySQL, the length must be less than 63 (2^6 - 1)
  // for foreign_key, substring 7 characters ('_ibfk_1')
  private def maxLength = 63 - 7

  def toSqlSentence: String = if (value.length > maxLength) {
    value.substring(0, maxLength)
  } else
    value
}
