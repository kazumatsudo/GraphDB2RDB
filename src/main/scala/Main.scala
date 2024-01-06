import domain.table.column.ColumnList
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import utils.{VertexQuery, VertexUtility}

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
    val vertexQuery = VertexQuery(g)
    val count = vertexQuery.countAll

    (0 to (count / 100).toInt).foreach { start =>
      // 1. get all vertices
      val verticesList = vertexQuery.getVerticesList(start.toInt, 100)

      /* 2. analyze vertex
       * TODO:
       *    - [x] vertex own
       *    - [ ] in edge
       *    - [ ] out edge
       */
      val analyzed = verticesList
        .map(vertex => VertexUtility.toColumnList(vertex))
        .reduce[ColumnList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }

      println(analyzed)
    }

    g.close()
  }
}
