import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import usecase.ByExhaustiveSearch
import utils.FileUtility

import scala.util.Using

object Main extends StrictLogging {

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
      val usecase = ByExhaustiveSearch(g)
      val (vertexResult, edgeResult) = usecase.execute

      vertexResult match {
        case Some((ddl, dml)) =>
          FileUtility.outputSql(
            config.getString("sql_ddl_vertex"),
            ddl.toSqlSentence
          )
          FileUtility.outputSql(
            config.getString("sql_dml_vertex"),
            dml.toSqlSentence
          )
        case None =>
      }
      displayOperationResult("generate vertex SQL", vertexResult.nonEmpty)

      edgeResult match {
        case Some((ddl, dml)) =>
          FileUtility.outputSql(
            config.getString("sql_ddl_edge"),
            ddl.toSqlSentence
          )
          FileUtility.outputSql(
            config.getString("sql_dml_edge"),
            dml.toSqlSentence
          )
        case None =>
      }
      displayOperationResult("generate edge SQL  ", edgeResult.nonEmpty)
    }
  }
}
