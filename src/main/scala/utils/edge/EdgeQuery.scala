package utils.edge

import com.typesafe.scalalogging.StrictLogging
import gremlin.scala.{Edge, GremlinScala}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import scala.util.control.NonFatal

final case class EdgeQuery(g: GraphTraversalSource) extends StrictLogging {

  /** get the number of all edges
   *
   * @return the number of all edges
   */
  def countAll: Long = GremlinScala(g.E()).count().head()

  /** get Edges List
   *
   * @param start The position to start retrieving Edges from (0-based index).
   * @param count The number of Edges to retrieve.
   * @return A list of Edges based on the specified pagination parameters.
   */
  def getEdgesList(start: Int, count: Int): Seq[Edge] = {
    require(start >= 0, "start must be positive.")
    require(count >= 0, "count must be positive.")

    try {
      GremlinScala(g.E()).range(start, start + count).toList()
    } catch {
      case NonFatal(e) =>
        logger.error(s"An exception has occurred when getEdgesList is called. start: $start, count: $count", e)
        throw e
    }
  }
}
