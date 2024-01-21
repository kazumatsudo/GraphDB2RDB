package domain.graph

import domain.table.ddl.column.{ColumnList, ColumnName, ColumnType}
import domain.table.ddl.{TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import gremlin.scala.Edge
import utils.Config

import scala.jdk.CollectionConverters.SetHasAsScala

case class GraphEdge(private val value: Edge, private val config: Config) {

  private val inVertex = value.inVertex()
  private val inVertexId = inVertex.id()
  private val inVertexLabel = inVertex.label()
  private val outVertex = value.outVertex()
  private val outVertexId = outVertex.id()
  private val outVertexLabel = outVertex.label()
  private val tableName = TableName(
    s"${config.tableName.edge}_${value.label()}_from_${outVertexLabel}_to_$inVertexLabel"
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
      val idColumn = Map(ColumnName(columnNameEdgeId) -> ColumnType.apply(id))
      val inVColumn =
        Map(ColumnName(columnNameEdgeInVId) -> ColumnType.apply(inVertexId))
      val outVColumn =
        Map(ColumnName(columnNameEdgeOutVId) -> ColumnType.apply(outVertexId))

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

      Map(
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

    val recordValue = Map(columnNameEdgeId -> value.id()) ++
      Map(columnNameEdgeInVId -> inVertexId) ++
      Map(columnNameEdgeOutVId -> outVertexId) ++
      propertyColumnList

    RecordList(
      Map(RecordKey(tableName, RecordId(id)) -> RecordValue(recordValue))
    )
  }
}
