package domain.table.ddl.attribute

final case class UniqueIndexName(private val value: String) extends AnyVal {

  def toSqlSentence: String = value
}
