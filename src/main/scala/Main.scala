import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import usecase.{ByExhaustiveSearch, UsingSpecificKeyList}
import utils.{FileUtility, JsonUtility}

import scala.util.{Failure, Success, Using}

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
    *
    * process
    *   - select analysis method
    *   - execute analysis method
    *   - output SQL
    * @param args
    */
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    Using(
      traversal().withRemote(
        config.getString("graphdb_remote_graph_properties")
      )
    ) { g =>
      /* select analysis method */
      sealed trait UsecaseCommand
      final case class UsecaseCommandByExhausiveSearch() extends UsecaseCommand
      final case class UsecaseCommandUsingSpecificKeyList()
          extends UsecaseCommand
      val usecaseCommand = config.getString("analysis_method") match {
        case "by_exhaustive_search"    => UsecaseCommandByExhausiveSearch()
        case "using_specific_key_list" => UsecaseCommandUsingSpecificKeyList()
        case value =>
          throw new IllegalArgumentException(
            s"analysis method must be by_exhaustive_search or using_specific_key_list. current analysis method: $value"
          )
      }
      val usecase = usecaseCommand match {
        case UsecaseCommandByExhausiveSearch() => ByExhaustiveSearch(g)
        case UsecaseCommandUsingSpecificKeyList() =>
          {
            for {
              jsonString <- FileUtility.readJson(
                config.getString(
                  "analysis_method_using_specific_key_list_filepath"
                )
              )
              request <- JsonUtility.readForUsingSpecificKeyListRequest(
                jsonString
              )
            } yield UsingSpecificKeyList(g, request)
          } match {
            case Failure(exception) => throw new Exception(exception)
            case Success(value)     => value
          }
      }

      /* execute analysis method */
      val (
        verticesDdlResult,
        verticesDmlResult,
        edgesDdlResult,
        edgesDmlResult
      ) =
        usecase.execute(checkUnique = false)

      /* output SQL */
      verticesDdlResult.foreach { vertexDdl =>
        FileUtility.outputSql(
          config.getString("sql_ddl_vertex"),
          vertexDdl.toSqlSentence
        )
      }
      displayOperationResult(
        "generate vertices DDL",
        verticesDdlResult.nonEmpty
      )

      verticesDmlResult.foreach { vertexDml =>
        FileUtility.outputSql(
          config.getString("sql_dml_vertex"),
          vertexDml.toSqlSentence
        )
      }
      displayOperationResult(
        "generate vertices DML",
        verticesDmlResult.nonEmpty
      )

      edgesDdlResult.foreach { edgesDdlResult =>
        FileUtility.outputSql(
          config.getString("sql_ddl_edge"),
          edgesDdlResult.toSqlSentence
        )
      }
      displayOperationResult("generate edges    DDL", edgesDdlResult.nonEmpty)

      edgesDmlResult.foreach { edgesDmlResult =>
        FileUtility.outputSql(
          config.getString("sql_dml_edge"),
          edgesDmlResult.toSqlSentence
        )
      }
      displayOperationResult("generate edges    DML", edgesDmlResult.nonEmpty)
    }
  }
}
