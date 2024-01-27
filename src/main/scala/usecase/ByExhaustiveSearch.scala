package usecase

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
  )(implicit ec: ExecutionContext): Future[UsecaseResponse] = {

    val vertexQuery = VertexQuery(g, config)
    val edgeQuery = EdgeQuery(g, config)

    for {
      // 1. generate vertex SQL
      count <- vertexQuery.countAll
      verticesResult <- Future.sequence {
        (0 to count.toInt).view.map(vertexQuery.getList(_, 1))
      }
      vertices = verticesResult.flatten
      vertexTableList <- toDdl(vertices, checkUnique)
      vertexRecordList <- toDml(vertices, checkUnique)

      // 2. generate edge SQL
      count <- edgeQuery.countAll
      edgesResult <- Future.sequence {
        (0 to count.toInt).view.map(edgeQuery.getList(_, 1))
      }
      edges = edgesResult.flatten
      edgeTableList <- toDdl(edges, checkUnique)
      edgeRecordList <- toDml(edges, checkUnique)
    } yield UsecaseResponse(
      vertexTableList,
      vertexRecordList,
      edgeTableList,
      edgeRecordList
    )
  }
}
