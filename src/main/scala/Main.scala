import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal

object Main {
  def main(args: Array[String]): Unit = {
    val g = traversal().withRemote("conf/remote-graph.properties")

    // https://tinkerpop.apache.org/docs/current/reference/#basic-gremlin
    val v1 = g.addV("person").property("name","marko").next()
    val v2 = g.addV("person").property("name", "stephen").next()
    g.V(v1).addE("knows").to(v2).property("weight",0.75).iterate()
    println(g.V().has("name", "marko").valueMap().toList)

    /* TODO: implement following
     *   1. get all vertex
     *   2. analyze vertex
     *   3. generate DDL
     */

    g.close()
  }
}
