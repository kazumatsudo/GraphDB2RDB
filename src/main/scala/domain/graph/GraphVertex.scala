package domain.graph

import domain.table.ddl.attribute.{ForeignKey, PrimaryKey, UniqueIndex}
import domain.table.ddl.column.{ColumnList, ColumnName, ColumnType}
import domain.table.ddl.{TableAttributes, TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import gremlin.scala._
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import utils.Config

import scala.jdk.CollectionConverters.MapHasAsScala

final case class GraphVertex(
    private val value: Vertex,
    private val config: Config,
    private val g: GraphTraversalSource
) extends GraphElement {

  private val tableName = TableName(
    s"${config.tableName.vertex}_${value.label()}"
  )
  private val columnNamePrefixProperty = config.columnName.prefixProperty
  private val columnNameVertexId = config.columnName.vertexId

  val id: AnyRef = value.id()

  /** convert to Database Table Information
    *
    * @return
    *   Database Table Information
    */
  override def toDdl: TableList =
    TableList {
      val idColumn =
        Map(ColumnName(columnNameVertexId) -> ColumnType.apply(value.id()))
//      val propertyColumn = value.valueMap.map { case (key, value) =>
//        ColumnName(s"$columnNamePrefixProperty$key") -> ColumnType.apply(value)
//      }
      val propertyColumn =
        GremlinScala(g.V(value)).valueMap
          .toList()
          .head
          .asScala
          .map { case (columnName, value) =>
            ColumnName(s"$columnNamePrefixProperty$columnName") -> ColumnType
              .apply(value)
          }
          .toMap
      Map(
        tableName -> (ColumnList(idColumn ++ propertyColumn),
        TableAttributes(
          PrimaryKey(Set(ColumnName(columnNameVertexId))),
          ForeignKey(Map.empty),
          UniqueIndex(Map.empty)
        ))
      )
    }

  override def toDml: RecordList = {
//    val propertyColumnList = value.valueMap.map { case (columnName, value) =>
//      (s"$columnNamePrefixProperty$columnName", value)
//    }
    val propertyColumnList =
      GremlinScala(g.V(value)).valueMap
        .toList()
        .head
        .asScala
        .map { case (columnName, value) =>
          (s"$columnNamePrefixProperty$columnName", value)
        }

    val recordValue =
      Map(columnNameVertexId -> id) ++ propertyColumnList

    RecordList(
      Map(RecordKey(tableName, RecordId(id)) -> RecordValue(recordValue))
    )
  }
}
