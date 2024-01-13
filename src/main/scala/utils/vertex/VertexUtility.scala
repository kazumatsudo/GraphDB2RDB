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
  private val columnNamePrefixProperty =
    config.getString("column_name_prefix_property")
  private val columnNamePrefixLabel =
    config.getString("column_name_prefix_label")

  /** convert to Database Table Information
    *
    * @param vertex
    *   [[Vertex]]
    * @return
    *   Database Table Information
    */
  def toDdl(vertex: Vertex): TableList =
    TableList {
      val idColumn = Map(
        ColumnName(config.getString("column_name_vertex_id")) -> ColumnType
          .apply(vertex.id())
      )
      val propertyColumn = vertex.valueMap.map { case (key, value) =>
        ColumnName(s"$columnNamePrefixProperty$key") -> ColumnType.apply(
          value
        )
      }
      val labelColumn = Map(
        ColumnName(
          s"$columnNamePrefixLabel${vertex.label()}"
        ) -> ColumnType.apply(true)
      )

      Map(tableName -> ColumnList(idColumn ++ propertyColumn ++ labelColumn))
    }

  def toDml(vertex: Vertex): String = {
    val (propertyColumnList, propertyValueList) = vertex.valueMap.unzip
    val valueListForSql = propertyValueList.map { value =>
      ColumnType.apply(value) match {
        case ColumnTypeBoolean   => value
        case ColumnTypeInt(_)    => value
        case ColumnTypeLong(_)   => value
        case ColumnTypeDouble(_) => value
        case ColumnTypeString(_) => s"\"$value\""
        case ColumnTypeUnknown   => s"\"$value\""
      }
    }

    val labelColumn = s"$columnNamePrefixLabel${vertex.label()}"

    s"INSERT INTO ${tableName.toSqlSentence} (${config.getString("column_name_vertex_id")}, ${propertyColumnList
        .map(columnName => s"$columnNamePrefixProperty$columnName")
        .mkString(", ")}, $labelColumn) VALUES (${vertex
        .id()}, ${valueListForSql.mkString(", ")}, true);"
  }
}
