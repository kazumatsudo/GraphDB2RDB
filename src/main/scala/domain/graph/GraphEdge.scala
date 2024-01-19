package domain.graph

import domain.table.ddl.column.{ColumnList, ColumnName, ColumnType}
import domain.table.ddl.{TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import gremlin.scala.Edge
import utils.Config

import scala.collection.parallel.CollectionConverters.ImmutableMapIsParallelizable
import scala.collection.parallel.immutable.ParMap
import scala.jdk.CollectionConverters.SetHasAsScala

case class GraphEdge(private val value: Edge, private val config: Config) {

  private val tableName = TableName(
    s"${config.tableName.edge}_${value.label()}"
  )
  private val columnNamePrefixProperty = config.columnName.prefixProperty

  private val columnNameEdgeId = config.columnName.edgeId
  private val columnNameEdgeInVId = config.columnName.edgeInVId
  private val columnNameEdgeOutVId = config.columnName.edgeOutVId

  private val id = value.id()

  /** convert to Database Table Information
    *
    * @return
    *   Database Table Information
    */
  def toDdl: TableList =
    TableList {
      val idColumn =
        ParMap(ColumnName(columnNameEdgeId) -> ColumnType.apply(id))
      val inVColumn =
        ParMap(
          ColumnName(columnNameEdgeInVId) -> ColumnType.apply(
            value.inVertex().id()
          )
        )
      val outVColumn =
        ParMap(
          ColumnName(columnNameEdgeOutVId) -> ColumnType.apply(
            value.outVertex().id()
          )
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

    val recordValue = ParMap(columnNameEdgeId -> value.id()) ++ ParMap(
      columnNameEdgeInVId -> value.inVertex().id()
    ) ++ ParMap(
      columnNameEdgeOutVId -> value.outVertex().id()
    ) ++ propertyColumnList

    RecordList(
      ParMap(RecordKey(tableName, RecordId(id)) -> RecordValue(recordValue))
    )
  }
}
