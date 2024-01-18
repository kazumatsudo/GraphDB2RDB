package domain.graph

import com.typesafe.config.ConfigFactory
import domain.table.ddl.column.{ColumnList, ColumnName, ColumnType}
import domain.table.ddl.{TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import gremlin.scala.Edge

import scala.collection.parallel.CollectionConverters.ImmutableMapIsParallelizable
import scala.collection.parallel.immutable.ParMap
import scala.jdk.CollectionConverters.SetHasAsScala

case class GraphEdge(private val value: Edge) {

  private val config = ConfigFactory.load()

  private val tableName = TableName(
    s"${config.getString("table_name_edge")}_${value.label()}"
  )
  private val columnNamePrefixProperty =
    config.getString("column_name_prefix_property")

  private val id = value.id()

  /** convert to Database Table Information
    *
    * @return
    *   Database Table Information
    */
  def toDdl: TableList =
    TableList {
      val idColumn = ParMap(
        ColumnName(config.getString("column_name_edge_id")) -> ColumnType
          .apply(id)
      )
      val inVColumn =
        ParMap(
          ColumnName(config.getString("column_name_edge_in_v_id")) -> ColumnType
            .apply(value.inVertex().id())
        )
      val outVColumn =
        ParMap(
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
        .par

      ParMap(
        tableName -> ColumnList(
          idColumn ++ inVColumn ++ outVColumn ++ propertyColumn
        )
      )
    }

  def toDml: RecordList = {
    // TODO: pull request for gremlin-scala
    val propertyColumnList = value
      .keys()
      .asScala
      .map { key =>
        (s"$columnNamePrefixProperty$key", value.value[Any](key))
      }
      .toMap
      .par

    val recordValue = ParMap(
      (config.getString("column_name_edge_id"), value.id())
    ) ++ ParMap(
      (config.getString("column_name_edge_in_v_id"), value.inVertex().id())
    ) ++ ParMap(
      (
        config.getString("column_name_edge_out_v_id"),
        value.outVertex().id()
      )
    ) ++ propertyColumnList

    RecordList(
      ParMap(RecordKey(tableName, RecordId(id)) -> RecordValue(recordValue))
    )
  }
}
