package domain.table

import domain.table.column.ColumnList

case class TableList(private val value: Map[TableName, ColumnList]) extends AnyVal {

  /** merges tableList in two Tables into one
   *
   * @param target target tableList
   * @return merged table list
   */
  def merge(target: TableList): TableList =
    TableList {
      value.foldLeft(target.value) { (accumulator, currentValue) =>
        val (tableName, columnList) = currentValue

        accumulator.updated(
          tableName,
          accumulator
            .get(tableName)
            .map(_.merge(columnList))
            .getOrElse(columnList)
        )
      }
    }

  def toSqlSentence: String =
    value.map { case (tableName, columnList) =>
      s"CREATE TABLE IF NOT EXISTS ${tableName.toSqlSentence} (${columnList.toSqlSentence});"
    }.mkString("\n")

}
