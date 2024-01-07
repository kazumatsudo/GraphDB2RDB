import com.typesafe.scalalogging.StrictLogging
import domain.table.TableList
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import utils.FileUtility
import utils.edge.{EdgeQuery, EdgeUtility}
import utils.vertex.{VertexQuery, VertexUtility}

import scala.util.Using
import scala.util.control.NonFatal

object Main extends StrictLogging {

  private def executeWithExceptionHandling(function: => Unit): Boolean = {
    try  {
      function
      true
    } catch {
      case NonFatal(e) =>
        logger.error(s"An exception occurred: ${e.getMessage}", e)
        false
    }
  }

  private def displayOperationResult(processName: String, result: Boolean): Unit = {
    if (result) {
      logger.info(s"$processName: success")
    } else {
      logger.warn(s"$processName: failure")
    }
  }

  /** generate DDL and Insert sentence from GraphDB
   * 1. generate vertex DDL
   * 2. generate edge DDL
   * 3. generate vertex Insert
   * 4. generate edge INSERT
   * @param args
   */
  def main(args: Array[String]): Unit = {
    val pageSize = 100

    Using(traversal().withRemote("conf/remote-graph.properties")) { g =>
      val vertexQuery = VertexQuery(g)
      val totalVertexPages = (vertexQuery.countAll / pageSize).toInt

      val edgeQuery = EdgeQuery(g)
      val totalEdgePages = (edgeQuery.countAll / pageSize).toInt

      // 1. generate vertex DDL
      val generateVertexDdlResult = executeWithExceptionHandling({
        val vertexAnalyzedResult = (0 to totalVertexPages).map { start =>
            vertexQuery
              .getVerticesList(start, pageSize)
              .map(vertex => VertexUtility.toTableList(vertex))
              .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }
          }
          .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }
        FileUtility.outputSql("ddl_vertex", vertexAnalyzedResult.toSqlSentence)
      })

      // 2. generate edge DDL
      val generateEdgeDdlResult = executeWithExceptionHandling({
        val edgeAnalyzedResult = (0 to totalEdgePages).map { start =>
            edgeQuery
              .getEdgesList(start, pageSize)
              .map(edge => EdgeUtility.toTableList(edge))
              .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }
          }
          .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }
        FileUtility.outputSql("ddl_edge", edgeAnalyzedResult.toSqlSentence)
      })

      // 3. generate vertex Insert
      val generateVertexInsertResult = executeWithExceptionHandling({
        val vertexInsertSentence = (0 to totalVertexPages).flatMap { start =>
          vertexQuery
            .getVerticesList(start, pageSize)
            .map(vertex => VertexUtility.toSqlSentence(vertex))
        }.mkString("\n")
        FileUtility.outputSql("insert_vertex", vertexInsertSentence)
      })

      // 4. generate edge INSERT
      val generateEdgeInsertResult = executeWithExceptionHandling({
        val edgeInsertSentence = (0 to totalEdgePages).flatMap { start =>
          edgeQuery
            .getEdgesList(start, pageSize)
            .map(edge => EdgeUtility.toSqlSentence(edge))
        }.mkString("\n")
        FileUtility.outputSql("insert_edge", edgeInsertSentence)
      })

      logger.info(s"generate SQL process is finished.")
      displayOperationResult("generate vertex DDL   ", generateVertexDdlResult)
      displayOperationResult("generate edge DDL     ", generateEdgeDdlResult)
      displayOperationResult("generate vertex INSERT", generateVertexInsertResult)
      displayOperationResult("generate edge INSERT  ", generateEdgeInsertResult)
    }
  }
}
