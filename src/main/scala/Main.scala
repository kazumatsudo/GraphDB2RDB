import com.typesafe.scalalogging.StrictLogging
import domain.table.TableList
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import utils.edge.{EdgeQuery, EdgeUtility}
import utils.vertex.{VertexQuery, VertexUtility}

object Main extends StrictLogging {

  /** generate DDL from GraphDB
   * 1. analyze vertex
   * 2. analyze edge
   * 3. generate DDL
   * 4. generate INSERT sentence
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

    val vertexInsertSentence = (0 to (vertexQuery.countAll / pageSize).toInt).flatMap { start =>
      vertexQuery
        .getVerticesList(start, pageSize)
        .map(vertex => VertexUtility.toSqlSentence(vertex))
    }.mkString("\n")
    val edgeInsertSentence = (0 to (edgeQuery.countAll / pageSize).toInt).flatMap { start =>
      edgeQuery
        .getEdgesList(start, pageSize)
        .map(edge => EdgeUtility.toSqlSentence(edge))
    }.mkString("\n")

    g.close()

    // 3. generate DDL
    logger.info("DDL")
    logger.info(vertexAnalyzedResult.toSqlSentence)
    logger.info(edgeAnalyzedResult.toSqlSentence)

    // 4. generate INSERT sentence
    logger.info("INSERT sentence")
    logger.info(vertexInsertSentence)
    logger.info(edgeInsertSentence)
  }
}
