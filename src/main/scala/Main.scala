import domain.table.TableList
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import utils.edge.{EdgeQuery, EdgeUtility}
import utils.vertex.{VertexQuery, VertexUtility}

object Main {

  /** generate DDL from GraphDB
   * 1. analyze vertex
   * 2. analyze edge
   * 3. generate DDL
   * @param args
   */
  def main(args: Array[String]): Unit = {
    val g = traversal().withRemote("conf/remote-graph.properties")

    val pageSize = 100

    // 1. analyze vertex
    val vertexQuery = VertexQuery(g)
    val vertexAnalyzedResult = (0 to (vertexQuery.countAll / pageSize).toInt).map { start =>
      vertexQuery
        .getVerticesList(start, pageSize)
        .map(vertex => VertexUtility.toTableList(vertex))
        .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }
    }
      .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }

    // 2. analyze edge
    val edgeQuery = EdgeQuery(g)
    val edgeAnalyzedResult = (0 to (edgeQuery.countAll / pageSize).toInt).map { start =>
      edgeQuery
        .getEdgesList(start, pageSize)
        .map(edge => EdgeUtility.toTableList(edge))
        .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }
    }
      .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }

    // 3. generate DDL
    println(vertexAnalyzedResult.toSqlSentence)
    println(edgeAnalyzedResult.toSqlSentence)

    g.close()
  }
}
