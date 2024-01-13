package utils.edge

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
import gremlin.scala.Edge
import utils.vertex.VertexUtility.config

import scala.jdk.CollectionConverters.SetHasAsScala

object EdgeUtility {

  private val config = ConfigFactory.load()

  // TODO: Set a more detailed table name
  private val tableName = TableName(config.getString("table_name_edge"))
  private val columnNamePrefixProperty =
    config.getString("column_name_prefix_property")
  private val columnNamePrefixLabel =
    config.getString("column_name_prefix_label")

  /** convert to Database Table Information
    *
    * @param edge
    *   [[Edge]]
    * @return
    *   Database Table Information
    */
  def toDdl(edge: Edge): TableList =
    TableList {
      val inVColumn =
        Map(
          ColumnName(config.getString("column_name_edge_in_v_id")) -> ColumnType
            .apply(edge.inVertex().id())
        )
      val outVColumn =
        Map(
          ColumnName(
            config.getString("column_name_edge_out_v_id")
          ) -> ColumnType.apply(edge.outVertex().id())
        )

      // TODO: pull request for gremlin-scala
      val propertyColumn = edge
        .keys()
        .asScala
        .map { key =>
          ColumnName(s"$columnNamePrefixProperty$key") -> ColumnType.apply(
            edge.value[Any](key)
          )
        }
        .toMap
      val labelColumn = Map(
        ColumnName(
          s"$columnNamePrefixLabel${edge.label()}"
        ) -> ColumnType.apply(true)
      )

      Map(
        tableName -> ColumnList(
          inVColumn ++ outVColumn ++ propertyColumn ++ labelColumn
        )
      )
    }

  def toSqlSentence(edge: Edge): String = {
    // TODO: pull request for gremlin-scala
    val (propertyColumnList, propertyValueList) =
      edge.keys().asScala.map { key => (key, edge.value[Any](key)) }.unzip
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

    val labelColumn = s"$columnNamePrefixLabel${edge.label()}"

    s"INSERT INTO ${tableName.toSqlSentence} (${config.getString("column_name_edge_in_v_id")}, ${config
        .getString("column_name_edge_out_v_id")}, ${propertyColumnList
        .map(columnName => s"$columnNamePrefixProperty$columnName")
        .mkString(", ")}, $labelColumn) VALUES (${edge.inVertex().id()}, ${edge.outVertex().id()}, ${valueListForSql
        .mkString(", ")}, true);"
  }
}
