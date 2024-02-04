package infrastructure

import com.typesafe.scalalogging.StrictLogging
import domain.graph.{GraphEdge, GraphVertex}
import gremlin.scala.{GremlinScala, Key}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import utils.Config

import scala.collection.SeqView
import scala.concurrent.{ExecutionContext, Future}

final case class VertexQuery(
    private val g: GraphTraversalSource,
    config: Config
) extends StrictLogging {

  /** get the number of all vertices
    *
    * @return
    *   the number of all vertices
    */
  def countAll()(implicit ec: ExecutionContext): Future[Long] = Future {
    GremlinScala(g.V()).count().head().longValue()
  }

  /** get in Vertices List
    *
    * @param edge
    *   target Edge
    * @return
    *   A list of Vertex
    */
  def getInVertexList(
      edge: GraphEdge
  )(implicit ec: ExecutionContext): Future[SeqView[GraphVertex]] = Future {
    GremlinScala(g.E(edge.id))
      .inV()
      .toList()
      .view
      .map(GraphVertex(_, config))
  }

  /** get Vertices List
    *
    * @param start
    *   The position to start retrieving Vertices from (0-based index).
    * @param count
    *   The number of Vertices to retrieve.
    * @return
    *   A list of Vertices based on the specified pagination parameters.
    */
  def getList(start: Int, count: Int)(implicit
      ec: ExecutionContext
  ): Future[SeqView[GraphVertex]] = Future {
    require(start >= 0, "start must be positive.")
    require(count >= 0, "count must be positive.")

    GremlinScala(g.V())
      .range(start, start + count)
      .toList()
      .view
      .map(GraphVertex(_, config))
  }

  /** get Vertices List searched by property key
    *
    * @param label
    *   vertex label
    * @param key
    *   vertex property key
    * @param value
    *   vertex property value
    * @return
    *   A list of Vertices.
    */
  def getListByPropertyKey(
      label: String,
      key: String,
      value: Any
  )(implicit ec: ExecutionContext): Future[SeqView[GraphVertex]] = Future {
    require(label.nonEmpty, "label must not be empty.")
    require(key.nonEmpty, "key must not be empty.")

    GremlinScala(g.V())
      .has(label, Key[Any](key), value)
      .toList()
      .view
      .map(GraphVertex(_, config))
  }

  /** get out Vertices List
    *
    * @param edge
    *   target Edge
    * @return
    *   A list of Vertex
    */
  def getOutVertexList(
      edge: GraphEdge
  )(implicit ec: ExecutionContext): Future[SeqView[GraphVertex]] = Future {
    GremlinScala(g.E(edge.id))
      .outV()
      .toList()
      .view
      .map(GraphVertex(_, config))
  }
}
