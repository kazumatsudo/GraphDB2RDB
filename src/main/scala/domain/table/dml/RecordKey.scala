package domain.table.dml

import domain.table.ddl.TableName

final case class RecordKey(private val value: (TableName, RecordId))
    extends AnyVal {
  def toSqlSentence: String = value._1.toSqlSentence
}
