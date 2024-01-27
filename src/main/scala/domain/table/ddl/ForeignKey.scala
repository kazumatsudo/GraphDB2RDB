package domain.table.ddl

import domain.table.ddl.column.ColumnName

final case class ForeignKey(
    private val value: Map[ColumnName, (TableName, ColumnName)]
) extends AnyVal {

  /** merges ForeignKey in two foreign key list into one
    *
    * @param target
    *   target ForeignKey
    * @param checkUnique
    *   whether the ForeignKey is unique.
    * @return
    *   merged foreign key list
    */
  def merge(target: ForeignKey, checkUnique: Boolean): ForeignKey = ForeignKey {
    if (checkUnique) {
      value.foldLeft(target.value) { (accumulator, currentValue) =>
        val (columnName, reference) = currentValue

        accumulator.get(columnName) match {
          case Some(currentReference) if currentReference == reference =>
            accumulator
          case Some(currentReference) =>
            throw new IllegalArgumentException(
              s"column name must be unique. column name: $columnName, detected values: $currentReference and $reference"
            )
          case None => accumulator.updated(columnName, reference)
        }
      }
    } else {
      value ++ target.value
    }
  }

  def toSqlSentence: Seq[String] = value.toSeq
    .sortBy { case (columnName, _) => columnName.toSqlSentence }
    .map { case (columnName, (referenceTableName, referenceColumnName)) =>
      s"FOREIGN KEY (${columnName.toSqlSentence}) REFERENCES ${referenceTableName.toSqlSentence}(${referenceColumnName.toSqlSentence})"
    }
}
