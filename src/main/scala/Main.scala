import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import domain.table.ddl.TableList
import domain.table.dml.RecordList
import infrastructure.{EdgeQuery, VertexQuery}
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import utils.FileUtility

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
      val totalVertexCount = vertexQuery.countAll.toInt

      val edgeQuery = EdgeQuery(g)
      val totalEdgeCount = edgeQuery.countAll.toInt

      // 1. generate vertex SQL
      val generateVertexSqlResult = executeWithExceptionHandling({
        val (ddl, dml) = (0 to totalVertexCount)
          .flatMap { start =>
            vertexQuery
              .getList(start, 1)
              .headOption
              .map(vertex =>
                (
                  vertex.toDdl,
                  vertex.toDml
                )
              )
          }
          .reduce[(TableList, RecordList)] {
            case (
                  (tableListAccumlator, dmlAccumlator),
                  (tableListCurrentValue, dmlCurrentValue)
                ) =>
              (
                tableListAccumlator.merge(tableListCurrentValue),
                dmlAccumlator.merge(dmlCurrentValue)
              )
          }
        FileUtility.outputSql(
          config.getString("sql_ddl_vertex"),
          ddl.toSqlSentence
        )
        FileUtility.outputSql(
          config.getString("sql_dml_vertex"),
          dml.toSqlSentence
        )
      })

      // 2. generate edge DDL
      val generateEdgeSqlResult = executeWithExceptionHandling({
        val (ddl, dml) = (0 to totalEdgeCount)
          .flatMap { start =>
            edgeQuery
              .getList(start, 1)
              .headOption
              .map(edge =>
                (
                  edge.toDdl,
                  edge.toDml
                )
              )
          }
          .reduce[(TableList, RecordList)] {
            case (
                  (tableListAccumlator, dmlAccumlator),
                  (tableListCurrentValue, dmlCurrentValue)
                ) =>
              (
                tableListAccumlator.merge(tableListCurrentValue),
                dmlAccumlator.merge(dmlCurrentValue)
              )
          }

        FileUtility.outputSql(
          config.getString("sql_ddl_edge"),
          ddl.toSqlSentence
        )
        FileUtility.outputSql(
          config.getString("sql_dml_edge"),
          dml.toSqlSentence
        )
      })

      logger.info(s"generate SQL process is finished.")
      displayOperationResult("generate vertex SQL", generateVertexSqlResult)
      displayOperationResult("generate edge SQL  ", generateEdgeSqlResult)
    }
  }
}
