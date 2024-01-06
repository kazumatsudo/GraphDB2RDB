package domain.table.column

import scala.collection.mutable

case class ColumnList(private val value: Map[ColumnName, ColumnType]) extends AnyVal {

  /** merges columnList in two columns into one
   *
   * @param target target columnList
   * @return merged column list
   */
  def merge(target: ColumnList): ColumnList = {
    val newValue = value.foldLeft(target.value) { (accumulator, currentValue) =>
      val (columnName, columnType) = currentValue

      accumulator.updated(
        columnName,
        accumulator
          .get(columnName)
          .map(_.merge(columnType))
          .getOrElse(columnType)
      )
    }
    ColumnList(newValue)
  }
}
