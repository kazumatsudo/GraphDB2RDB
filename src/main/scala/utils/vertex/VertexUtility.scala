package utils.vertex

import domain.table.{TableList, TableName}
import domain.table.column.{ColumnList, ColumnName, ColumnType}
import gremlin.scala._

object VertexUtility {

  // TODO: Set a more detailed table name
  private val tableName = TableName("vertex")

  /** convert to Database Table Information
   *
   * @param vertex [[Vertex]]
   * @return Database Table Information
   */
  def toTableList(vertex: Vertex): TableList =
    TableList {
      val idColumn = Map(ColumnName("id") -> ColumnType.apply(vertex.id()))
      val column = vertex.valueMap.map { case (key, value) =>
        ColumnName(key) -> ColumnType.apply(value)
      }

      Map(tableName -> ColumnList(idColumn ++ column))
    }

  def toSqlSentence(vertex: Vertex): String = {
    val (columnList, valueList) = vertex.valueMap.unzip
    s"INSERT INTO ${tableName.toSqlSentence} (id, ${columnList.mkString(",")}) VALUES (${vertex.id()}, ${valueList.mkString(", ")});"
  }
}
