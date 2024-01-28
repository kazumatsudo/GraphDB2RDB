package domain.table.ddl.attribute

import domain.table.ddl.TableName
import domain.table.ddl.column.ColumnName

import scala.collection.View

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

  def toSqlSentenceView: View[String] = value.toSeq
    .sortBy { case (columnName, _) => columnName.toSqlSentence }
    .map { case (columnName, (referenceTableName, referenceColumnName)) =>
      s"FOREIGN KEY (${columnName.toSqlSentence}) REFERENCES ${referenceTableName.toSqlSentence}(${referenceColumnName.toSqlSentence})"
    }
    .view
}
