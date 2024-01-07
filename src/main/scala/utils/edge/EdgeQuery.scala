package utils.edge

import gremlin.scala.GremlinScala
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

final case class EdgeQuery(g: GraphTraversalSource) {

  /** get the number of all edges
   *
   * @return the number of all edges
   */
  def countAll: Long = GremlinScala(g.E()).count().head()
}
