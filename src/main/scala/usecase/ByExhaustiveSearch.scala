package usecase

import domain.table.ddl.TableList
import domain.table.dml.RecordList
import infrastructure.{EdgeQuery, VertexQuery}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

/** analyze all Vertices and Edges
  *
  * pros:
  *   - no advance preparation required
  * cons:
  *   - inefficient (execute full search all vertices and edges count times)
  *
  * @param g
  *   [[GraphTraversalSource]]
  */
final case class ByExhaustiveSearch(
    override protected val g: GraphTraversalSource
) extends UsecaseBase {

  override def execute(checkUnique: Boolean): UsecaseResponse = {

    // 1. generate vertex SQL
    val (verticesDdl, verticesDml) = {
      val vertexQuery = VertexQuery(g)
      val totalVertexCount = vertexQuery.countAll.toInt

      (0 to totalVertexCount).view
        .flatMap { start =>
          vertexQuery
            .getList(start, 1)
            .headOption
            .map(vertex =>
              (
                vertex.toDdl,
                vertex.toDml
              )
            )
        }
        .reduce[(TableList, RecordList)] {
          case (
                (tableListAccumlator, dmlAccumlator),
                (tableListCurrentValue, dmlCurrentValue)
              ) =>
            (
              tableListAccumlator.merge(tableListCurrentValue),
              dmlAccumlator.merge(dmlCurrentValue, checkUnique)
            )
        }
    }

    // 2. generate edge SQL
    val (edgesDdl, edgesDml) = {
      val edgeQuery = EdgeQuery(g)
      val totalEdgeCount = edgeQuery.countAll.toInt

      (0 to totalEdgeCount).view
        .flatMap { start =>
          edgeQuery
            .getList(start, 1)
            .headOption
            .map(edge =>
              (
                edge.toDdl,
                edge.toDml
              )
            )
        }
        .reduce[(TableList, RecordList)] {
          case (
                (tableListAccumlator, dmlAccumlator),
                (tableListCurrentValue, dmlCurrentValue)
              ) =>
            (
              tableListAccumlator.merge(tableListCurrentValue),
              dmlAccumlator.merge(dmlCurrentValue, checkUnique)
            )
        }
    }

    UsecaseResponse(
      Some(verticesDdl),
      Some(verticesDml),
      Some(edgesDdl),
      Some(edgesDml)
    )
  }
}
