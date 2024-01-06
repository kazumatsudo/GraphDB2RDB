package utils

import gremlin.scala.GremlinScala
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

final case class VertexUtilities(g: GraphTraversalSource) {

  /** get the number of all vertices
   *
   * @return the number of all vertices
   */
  def countAll: Long = GremlinScala(g.V()).count().head()
}
