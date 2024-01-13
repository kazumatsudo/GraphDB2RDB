package domain.graph

import com.typesafe.config.ConfigFactory
import domain.table.column._
import domain.table.{TableList, TableName}
import gremlin.scala.Edge

import scala.jdk.CollectionConverters.SetHasAsScala

case class GraphEdge(private val value: Edge) {

  private val config = ConfigFactory.load()

  // TODO: Set a more detailed table name
  private val tableName = TableName(config.getString("table_name_edge"))
  private val columnNamePrefixProperty =
    config.getString("column_name_prefix_property")
  private val columnNamePrefixLabel =
    config.getString("column_name_prefix_label")

  /** convert to Database Table Information
    *
    * @return
    *   Database Table Information
    */
  def toDdl: TableList =
    TableList {
      val inVColumn =
        Map(
          ColumnName(config.getString("column_name_edge_in_v_id")) -> ColumnType
            .apply(value.inVertex().id())
        )
      val outVColumn =
        Map(
          ColumnName(
            config.getString("column_name_edge_out_v_id")
          ) -> ColumnType.apply(value.outVertex().id())
        )

      // TODO: pull request for gremlin-scala
      val propertyColumn = value
        .keys()
        .asScala
        .map { key =>
          ColumnName(s"$columnNamePrefixProperty$key") -> ColumnType.apply(
            value.value[Any](key)
          )
        }
        .toMap
      val labelColumn = Map(
        ColumnName(
          s"$columnNamePrefixLabel${value.label()}"
        ) -> ColumnType.apply(true)
      )

      Map(
        tableName -> ColumnList(
          inVColumn ++ outVColumn ++ propertyColumn ++ labelColumn
        )
      )
    }

  def toDml: String = {
    // TODO: pull request for gremlin-scala
    val (propertyColumnList, propertyValueList) =
      value.keys().asScala.map { key => (key, value.value[Any](key)) }.unzip
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
    val labelColumn = s"$columnNamePrefixLabel${value.label()}"

    val (keys, values) = (
      Seq(
        (config.getString("column_name_edge_in_v_id"), value.inVertex().id())
      ) ++ Seq(
        (
          config.getString("column_name_edge_out_v_id"),
          value.outVertex().id()
        )
      ) ++ propertyColumnList
        .map(columnName => s"$columnNamePrefixProperty$columnName")
        .zip(valueListForSql) ++ Seq(
        (labelColumn, true)
      )
    ).unzip

    s"INSERT INTO ${tableName.toSqlSentence} (${keys.mkString(", ")}) VALUES (${values.mkString(", ")});"
  }
}
