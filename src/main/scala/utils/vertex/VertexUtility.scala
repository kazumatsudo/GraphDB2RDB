package utils.vertex

import domain.table.{TableList, TableName}
import domain.table.column.{ColumnList, ColumnName, ColumnType}
import gremlin.scala._

object VertexUtility {

  /** convert to Database Table Information
   *
   * @param vertex [[Vertex]]
   * @return Database Table Information
   */
  def toTableList(vertex: Vertex): TableList =
    TableList {
      val columnList = ColumnList(vertex.valueMap.map { case (key, value) =>
        ColumnName(key) -> ColumnType.apply(value)
      })

      // TODO: Set a more detailed table name
      val tableName = TableName("vertex")
      Map(tableName -> columnList)
    }
}
