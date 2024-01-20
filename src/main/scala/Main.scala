import com.typesafe.scalalogging.StrictLogging
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import usecase.{ByExhaustiveSearch, UsingSpecificKeyList}
import utils.{Config, FileUtility, JsonUtility}

import scala.util.control.NonFatal
import scala.util.{Failure, Success, Using}

object Main extends StrictLogging {

  private val config: Config = Config.default

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

  def execute(g: GraphTraversalSource): Unit = {
    /* select analysis method */
    sealed trait UsecaseCommand
    final case class UsecaseCommandByExhausiveSearch() extends UsecaseCommand
    final case class UsecaseCommandUsingSpecificKeyList() extends UsecaseCommand
    val usecaseCommand = config.analysysMethod.value match {
      case "by_exhaustive_search"    => UsecaseCommandByExhausiveSearch()
      case "using_specific_key_list" => UsecaseCommandUsingSpecificKeyList()
      case value =>
        throw new IllegalArgumentException(
          s"analysis method must be by_exhaustive_search or using_specific_key_list. current analysis method: $value"
        )
    }
    val usecase = usecaseCommand match {
      case UsecaseCommandByExhausiveSearch() => ByExhaustiveSearch(g, config)
      case UsecaseCommandUsingSpecificKeyList() =>
        {
          for {
            jsonString <- FileUtility.readJson(
              config.analysysMethod.usingSpecificKeyListFilepath
            )
            request <- JsonUtility.readForUsingSpecificKeyListRequest(
              jsonString
            )
          } yield UsingSpecificKeyList(g, config, request)
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
    ) = usecase.execute(checkUnique = false)

    /* output SQL */
    verticesDdlResult.foreach { vertexDdl =>
      FileUtility.writeSql(
        config.sql.ddlVertex,
        vertexDdl.toSqlSentence.mkString("\n")
      )
    }
    displayOperationResult(
      "generate vertices DDL",
      verticesDdlResult.nonEmpty
    )

    verticesDmlResult.foreach { vertexDml =>
      FileUtility.writeSql(
        config.sql.dmlVertex,
        vertexDml.toSqlSentence.mkString("\n")
      )
    }
    displayOperationResult(
      "generate vertices DML",
      verticesDmlResult.nonEmpty
    )

    edgesDdlResult.foreach { edgesDdlResult =>
      FileUtility.writeSql(
        config.sql.ddlEdge,
        edgesDdlResult.toSqlSentence.mkString("\n")
      )
    }
    displayOperationResult("generate edges    DDL", edgesDdlResult.nonEmpty)

    edgesDmlResult.foreach { edgesDmlResult =>
      FileUtility.writeSql(
        config.sql.dmlEdge,
        edgesDmlResult.toSqlSentence.mkString("\n")
      )
    }
    displayOperationResult("generate edges    DML", edgesDmlResult.nonEmpty)
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
    Using(traversal().withRemote(config.graphDb.remoteGraphProperties))(execute)
      .recover { case NonFatal(e) =>
        logger.error(s"${e.getMessage}", e)
        sys.exit(1)
      }
  }
}
