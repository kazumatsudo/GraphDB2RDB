package utils

import com.typesafe.scalalogging.StrictLogging
import domain.graph.GraphVertex
import gremlin.scala.GremlinScala
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import scala.util.control.NonFatal

final case class VertexQuery(private val g: GraphTraversalSource)
    extends StrictLogging {

  /** get the number of all vertices
    *
    * @return
    *   the number of all vertices
    */
  def countAll: Long = GremlinScala(g.V()).count().head()

  /** get Vertices List
    *
    * @param start
    *   The position to start retrieving Vertices from (0-based index).
    * @param count
    *   The number of Vertices to retrieve.
    * @return
    *   A list of Vertices based on the specified pagination parameters.
    */
  def getList(start: Int, count: Int): Seq[GraphVertex] = {
    require(start >= 0, "start must be positive.")
    require(count >= 0, "count must be positive.")

    try {
      GremlinScala(g.V()).range(start, start + count).toList().map(GraphVertex)
    } catch {
      case NonFatal(e) =>
        logger.error(
          s"An exception has occurred when getVerticesList is called. start: $start, count: $count",
          e
        )
        throw e
    }
  }
}
