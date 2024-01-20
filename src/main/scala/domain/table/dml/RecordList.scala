package domain.table.dml

import scala.collection.View

final case class RecordList(private val value: Map[RecordKey, RecordValue])
    extends {

  /** merges recordList in two Records into one
    *
    * @param target
    *   target RecordList
    * @param checkUnique
    *   whether the RecordKey is unique.
    * @return
    *   merged record list
    */
  def merge(target: RecordList, checkUnique: Boolean): RecordList = RecordList {
    if (checkUnique) {
      value.foldLeft(target.value) { (accumulator, currentValue) =>
        val (recordKey, recordValue) = currentValue

        accumulator.get(recordKey) match {
          case Some(currentRecordValue) if currentRecordValue == recordValue =>
            accumulator
          case Some(currentRecordValue) =>
            throw new IllegalArgumentException(
              s"record key must be unique. record key: $recordKey, detected values: $currentRecordValue and $recordValue"
            )
          case None => accumulator.updated(recordKey, recordValue)
        }
      }
    } else {
      value ++ target.value
    }
  }

  def toSqlSentence: View[String] = value.map { case (recordKey, recordValue) =>
    val (keys, values) = recordValue.toSqlSentence
    s"INSERT INTO ${recordKey.toSqlSentence} ($keys) VALUES ($values);"
  }.view
}
