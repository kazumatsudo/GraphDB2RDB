package domain.table.column

import scala.collection.mutable

case class ColumnList(private val value: Map[ColumnName, ColumnType]) extends AnyVal {

  /** merges columnList in two columns into one
   *
   * @param target target columnList
   * @return merged column list
   */
  def merge(target: ColumnList): ColumnList = ColumnList {
    val newValue = mutable.Map.empty[ColumnName, ColumnType]

    newValue ++= value
    target.value.foreach { case (columnName, columnType) =>
      val newColumnType = newValue.get(columnName) match {
        case Some(existedColumnType) => existedColumnType.merge(columnType)
        case None => columnType
      }

      newValue ++= Map(columnName -> newColumnType)
    }

    newValue.toMap
  }
}
