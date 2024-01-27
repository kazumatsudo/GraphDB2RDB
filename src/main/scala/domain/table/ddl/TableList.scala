package domain.table.ddl

import domain.table.ddl.column.ColumnList

import scala.collection.View

case class TableList(
    private val value: Map[TableName, (ColumnList, TableAttribute)]
) extends AnyVal {

  /** merges tableList in two Tables into one
    *
    * @param target
    *   target tableList
    * @return
    *   merged table list
    */
  def merge(target: TableList): TableList =
    TableList {
      value.foldLeft(target.value) { (accumulator, currentValue) =>
        val (tableName, (columnList, tableAttribute)) = currentValue

        accumulator.updated(
          tableName,
          accumulator
            .get(tableName)
            .map { case (accumulatorColumnList, accumulatorTableAttribute) =>
              (
                accumulatorColumnList.merge(columnList),
                accumulatorTableAttribute
              )
            }
            .getOrElse((columnList, tableAttribute))
        )
      }
    }

  def toSqlSentence: View[String] = value.map {
    case (tableName, (columnList, _)) =>
      s"CREATE TABLE IF NOT EXISTS ${tableName.toSqlSentence} (${columnList.toSqlSentence});"
  }.view

}
