package domain.graph

import com.typesafe.config.ConfigFactory
import domain.table.ddl.column.{
  ColumnList,
  ColumnName,
  ColumnType
}
import domain.table.ddl.{TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
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

  def toDml: RecordList = {
    val id = value.id()

    // TODO: pull request for gremlin-scala
    val propertyColumnList = value
      .keys()
      .asScala
      .map { key =>
        (s"$columnNamePrefixProperty$key", value.value[Any](key))
      }
      .toMap

    val labelColumn = s"$columnNamePrefixLabel${value.label()}"

    val recordValue = Map(
      (config.getString("column_name_edge_in_v_id"), value.inVertex().id())
    ) ++ Map(
      (
        config.getString("column_name_edge_out_v_id"),
        value.outVertex().id()
      )
    ) ++ propertyColumnList ++ Map((labelColumn, true))

    RecordList(
      Map((RecordKey(tableName, RecordId(id)), RecordValue(recordValue)))
    )
  }
}
