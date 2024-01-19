package usecase

import infrastructure.{EdgeQuery, VertexQuery}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import java.util.concurrent.Executors.newFixedThreadPool
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

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

  // set gremlin server connection pool max size or less
  implicit private val ec: ExecutionContext =
    ExecutionContext.fromExecutor(newFixedThreadPool(1))

  override def execute(checkUnique: Boolean): UsecaseResponse = {

    // 1. generate vertex SQL
    val vertexQuery = VertexQuery(g)
    val edgeQuery = EdgeQuery(g)

    val ((vertexTableList, vertexRecordList), (edgeTableList, edgeRecordList)) =
      Await.result(
        for {
          vertexResult <- for {
            count <- vertexQuery.countAll
            vertices <- Future
              .sequence {
                (0 to count.toInt).view.map { start =>
                  vertexQuery
                    .getList(start, 1)
                    .map(_.map(vertex => (vertex.toDdl, vertex.toDml)))
                }
              }
              .map(_.map(foldLeft(_, checkUnique)))
          } yield foldLeft(vertices, checkUnique)
          edgeResult <- for {
            count <- edgeQuery.countAll
            edges <- Future
              .sequence {
                (0 to count.toInt).view.map { start =>
                  edgeQuery
                    .getList(start, 1)
                    .map(_.map(edge => (edge.toDdl, edge.toDml)))
                }
              }
              .map(_.map(foldLeft(_, checkUnique)))
          } yield foldLeft(edges, checkUnique)
        } yield (vertexResult, edgeResult),
        Duration.Inf
      )

    UsecaseResponse(
      Some(vertexTableList),
      Some(vertexRecordList),
      Some(edgeTableList),
      Some(edgeRecordList)
    )
  }
}
