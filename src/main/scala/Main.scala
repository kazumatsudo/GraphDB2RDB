import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import domain.table.TableList
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import utils.{EdgeQuery, FileUtility, VertexQuery}

import scala.util.Using
import scala.util.control.NonFatal

object Main extends StrictLogging {

  private def executeWithExceptionHandling(function: => Unit): Boolean = {
    try {
      function
      true
    } catch {
      case NonFatal(e) =>
        logger.error(s"An exception occurred: ${e.getMessage}", e)
        false
    }
  }

  private def displayOperationResult(
      processName: String,
      result: Boolean
  ): Unit = {
    if (result) {
      logger.info(s"$processName: success")
    } else {
      logger.warn(s"$processName: failure")
    }
  }

  /** generate DDL and Insert sentence from GraphDB
    *   1. generate vertex SQL 2. generate edge SQL
    * @param args
    */
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    Using(
      traversal().withRemote(
        config.getString("graphdb_remote_graph_properties")
      )
    ) { g =>
      val vertexQuery = VertexQuery(g)
      val edgeQuery = EdgeQuery(g)

      // 1. generate vertex SQL
      val generateVertexSqlResult = executeWithExceptionHandling({
        var vertexCount = 0
        var ddl: TableList = TableList(Map.empty)
        var dml: String = ""

        while (vertexQuery.getVertexByOrder(vertexCount).nonEmpty) {
          val vertex = vertexQuery.getVertexByOrder(vertexCount).get

          ddl = ddl.merge(vertex.toDdl)
          dml = s"$dml\n${vertex.toDml}"

          vertexCount = vertexCount + 1
        }

        FileUtility.outputSql(
          config.getString("sql_ddl_vertex"),
          ddl.toSqlSentence
        )
        FileUtility.outputSql(
          config.getString("sql_dml_vertex"),
          dml
        )
      })

      // 2. generate edge DDL
      val generateEdgeSqlResult = executeWithExceptionHandling({
        var edgeCount = 0
        var ddl: TableList = TableList(Map.empty)
        var dml: String = ""

        while (edgeQuery.getEdgeByOrder(edgeCount).nonEmpty) {
          val edge = edgeQuery.getEdgeByOrder(edgeCount).get

          ddl = ddl.merge(edge.toDdl)
          dml = s"$dml\n${edge.toDml}"

          edgeCount = edgeCount + 1
        }

        FileUtility.outputSql(
          config.getString("sql_ddl_edge"),
          ddl.toSqlSentence
        )
        FileUtility.outputSql(
          config.getString("sql_dml_edge"),
          dml
        )
      })

      logger.info(s"generate SQL process is finished.")
      displayOperationResult("generate vertex SQL", generateVertexSqlResult)
      displayOperationResult("generate edge SQL  ", generateEdgeSqlResult)
    }
  }
}
