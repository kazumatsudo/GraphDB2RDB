package usecase

import domain.table.ddl.TableList
import domain.table.dml.RecordList
import infrastructure.{EdgeQuery, VertexQuery}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import utils.Config

import scala.concurrent.{ExecutionContext, Future}

/** analyze all Vertices and Edges
  *
  * pros:
  *   - no advance preparation required
  * cons:
  *   - inefficient (execute full search all vertices and edges count times)
  *
  * @param g
  *   [[GraphTraversalSource]]
  * @param config
  *   [[Config]]
  */
final case class ByExhaustiveSearch(
    override protected val g: GraphTraversalSource,
    override protected val config: Config
) extends UsecaseBase {

  override def execute(
      checkUnique: Boolean
  )(implicit ec: ExecutionContext): Future[UsecaseResponse] = Future {

    // 1. generate vertex SQL
    val (verticesDdl, verticesDml) = {
      val vertexQuery = VertexQuery(g, config)
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
      val edgeQuery = EdgeQuery(g, config)
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
      verticesDdl,
      verticesDml,
      edgesDdl,
      edgesDml
    )
  }
}
