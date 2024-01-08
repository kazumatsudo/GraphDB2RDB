package utils.vertex

import com.typesafe.config.ConfigFactory
import domain.table.{TableList, TableName}
import domain.table.column.{
  ColumnList,
  ColumnName,
  ColumnType,
  ColumnTypeBoolean,
  ColumnTypeDouble,
  ColumnTypeInt,
  ColumnTypeLong,
  ColumnTypeString,
  ColumnTypeUnknown
}
import gremlin.scala._

object VertexUtility {

  private val config = ConfigFactory.load()

  // TODO: Set a more detailed table name
  private val tableName = TableName(config.getString("table_name_vertex"))

  /** convert to Database Table Information
    *
    * @param vertex
    *   [[Vertex]]
    * @return
    *   Database Table Information
    */
  def toTableList(vertex: Vertex): TableList =
    TableList {
      val idColumn = Map(
        ColumnName(config.getString("column_name_vertex_id")) -> ColumnType
          .apply(vertex.id())
      )
      val column = vertex.valueMap.map { case (key, value) =>
        ColumnName(key) -> ColumnType.apply(value)
      }

      Map(tableName -> ColumnList(idColumn ++ column))
    }

  def toSqlSentence(vertex: Vertex): String = {
    val (columnList, valueList) = vertex.valueMap.unzip
    val valueListForSql = valueList.map { value =>
      ColumnType.apply(value) match {
        case ColumnTypeBoolean   => value
        case ColumnTypeInt(_)    => value
        case ColumnTypeLong(_)   => value
        case ColumnTypeDouble(_) => value
        case ColumnTypeString(_) => s"\"$value\""
        case ColumnTypeUnknown   => s"\"$value\""
      }
    }
    s"INSERT INTO ${tableName.toSqlSentence} (${config.getString("column_name_vertex_id")}, ${columnList
        .mkString(",")}) VALUES (${vertex
        .id()}, ${valueListForSql.mkString(", ")});"
  }
}
