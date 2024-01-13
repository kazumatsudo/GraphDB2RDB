package utils

import com.typesafe.scalalogging.StrictLogging
import domain.graph.GraphEdge
import gremlin.scala.GremlinScala
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import scala.util.control.NonFatal

final case class EdgeQuery(private val g: GraphTraversalSource)
    extends StrictLogging {

  /** get the number of all edges
    *
    * @return
    *   the number of all edges
    */
  def countAll: Long = GremlinScala(g.E()).count().head()

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

  def getEdgeByOrder(position: Int): Option[GraphEdge] = {
    require(position >= 0, "position must be positive.")

    try {
      val graphTraversal = g.E().range(position, position + 1)
      if (graphTraversal.hasNext) {
        Some(GraphEdge(graphTraversal.next()))
      } else {
        None
      }
    } catch {
      case NonFatal(e) =>
        logger.error(
          s"An exception has occurred when getEdgesList is called. position: $position",
          e
        )
        throw e
    }
  }
}
