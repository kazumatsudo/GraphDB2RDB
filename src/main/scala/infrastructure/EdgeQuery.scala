package infrastructure

import com.typesafe.scalalogging.StrictLogging
import domain.graph.{GraphEdge, GraphVertex}
import gremlin.scala.GremlinScala
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import scala.collection.SeqView
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

final case class EdgeQuery(private val g: GraphTraversalSource)
    extends StrictLogging {

  /** get the number of all edges
    *
    * @return
    *   the number of all edges
    */
  def countAll: Long = GremlinScala(g.E()).count().head()

  /** get in Edges List
    *
    * @param vertex
    *   target Vertex
    * @return
    *   A list of Edge
    */
  def getInEdgeList(
      vertex: GraphVertex
  )(implicit ec: ExecutionContext): Future[SeqView[GraphEdge]] = Future {
    GremlinScala(g.V(vertex.id)).inE().toList().view.map(GraphEdge)
  }

  /** get Edges List
    *
    * @param start
    *   The position to start retrieving Edges from (0-based index).
    * @param count
    *   The number of Edges to retrieve.
    * @return
    *   A list of Edges based on the specified pagination parameters.
    */
  def getList(start: Int, count: Int): Seq[GraphEdge] = {
    require(start >= 0, "start must be positive.")
    require(count >= 0, "count must be positive.")

    try {
      GremlinScala(g.E()).range(start, start + count).toList().map(GraphEdge)
    } catch {
      case NonFatal(e) =>
        logger.error(
          s"An exception has occurred when getEdgesList is called. start: $start, count: $count",
          e
        )
        throw e
    }
  }

  /** get out Edges List
    *
    * @param vertex
    *   target Vertex
    * @return
    *   A list of Edge
    */
  def getOutEdgeList(
      vertex: GraphVertex
  )(implicit ec: ExecutionContext): Future[SeqView[GraphEdge]] = Future {
    GremlinScala(g.V(vertex.id)).outE().toList().view.map(GraphEdge)
  }
}
