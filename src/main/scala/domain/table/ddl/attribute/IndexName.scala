package domain.table.ddl.attribute

final case class IndexName(private val value: String) extends AnyVal {

  def toSqlSentence: String = value
}
