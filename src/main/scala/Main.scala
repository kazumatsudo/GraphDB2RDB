import com.typesafe.scalalogging.StrictLogging
import domain.table.TableList
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import utils.FileUtility
import utils.edge.{EdgeQuery, EdgeUtility}
import utils.vertex.{VertexQuery, VertexUtility}

object Main extends StrictLogging {

  /** generate DDL and Insert sentence from GraphDB
   * 1. generate vertex DDL
   * 2. generate edge DDL
   * 3. generate vertex Insert sentence
   * 4. generate edge INSERT sentence
   * @param args
   */
  def main(args: Array[String]): Unit = {
    val g = traversal().withRemote("conf/remote-graph.properties")
    val pageSize = 100

    // 1. generate vertex DDL
    val vertexQuery = VertexQuery(g)
    val vertexAnalyzedResult = (0 to (vertexQuery.countAll / pageSize).toInt).map { start =>
      vertexQuery
        .getVerticesList(start, pageSize)
        .map(vertex => VertexUtility.toTableList(vertex))
        .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }
    }
      .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }
    FileUtility.outputSql("ddl_vertex", vertexAnalyzedResult.toSqlSentence)

    // 2. generate edge DDL
    val edgeQuery = EdgeQuery(g)
    val edgeAnalyzedResult = (0 to (edgeQuery.countAll / pageSize).toInt).map { start =>
      edgeQuery
        .getEdgesList(start, pageSize)
        .map(edge => EdgeUtility.toTableList(edge))
        .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }
    }
      .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }
    FileUtility.outputSql("ddl_edge", edgeAnalyzedResult.toSqlSentence)

    // 3. generate vertex Insert sentence
    val vertexInsertSentence = (0 to (vertexQuery.countAll / pageSize).toInt).flatMap { start =>
      vertexQuery
        .getVerticesList(start, pageSize)
        .map(vertex => VertexUtility.toSqlSentence(vertex))
    }.mkString("\n")
    FileUtility.outputSql("insert_vertex", vertexInsertSentence)

    // 4. generate edge INSERT sentence
    val edgeInsertSentence = (0 to (edgeQuery.countAll / pageSize).toInt).flatMap { start =>
      edgeQuery
        .getEdgesList(start, pageSize)
        .map(edge => EdgeUtility.toSqlSentence(edge))
    }.mkString("\n")
    FileUtility.outputSql("insert_edge", edgeInsertSentence)

    g.close()
  }
}
