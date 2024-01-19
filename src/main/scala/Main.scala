import com.typesafe.scalalogging.StrictLogging
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import usecase.{ByExhaustiveSearch, UsingSpecificKeyList}
import utils.{FileUtility, JsonUtility}

import java.util.concurrent.Executors.newFixedThreadPool
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Using}

object Main extends StrictLogging {

  // set gremlin server connection pool max size or less
  implicit private val ec: ExecutionContext =
    ExecutionContext.fromExecutor(newFixedThreadPool(1))

  private val config: Config = Config.default

  private def displayOperationResult(result: Boolean): Unit = {
    if (result) {
      logger.info("generate SQL: success")
    } else {
      logger.error("generate SQL: failure")
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
    Using(traversal().withRemote(config.graphDb.remoteGraphProperties)) { g =>
      /* select analysis method */
      sealed trait UsecaseCommand
      final case class UsecaseCommandByExhausiveSearch() extends UsecaseCommand
      final case class UsecaseCommandUsingSpecificKeyList()
          extends UsecaseCommand
      val usecaseCommand = config.analysysMethod.value match {
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
                config.analysysMethod.using_specific_key_list_filepath
              )
              request <- JsonUtility.parseForUsingSpecificKeyListRequest(
                jsonString
              )
            } yield UsingSpecificKeyList(g, request)
          } match {
            case Failure(exception) => throw new Exception(exception)
            case Success(value)     => value
          }
      }

      /* execute analysis method */
      val result = usecase.execute(checkUnique = false)

      result.onComplete {
        case Failure(exception) =>
          logger.error(s"${exception.getMessage}", exception)

          displayOperationResult(result = false)
          sys.exit(1)
        case Success(value) =>
          /* output SQL */
          FileUtility.outputSql(
            config.sql.ddl_edge,
            value.verticesDdl.toSqlSentence
          )
          FileUtility.outputSql(
            config.sql.dml_edge,
            value.verticesDml.toSqlSentence
          )
          FileUtility.outputSql(
            config.sql.dml_vertex,
            value.edgesDdl.toSqlSentence
          )
          FileUtility.outputSql(
            config.sql.dml_vertex,
            value.edgesDml.toSqlSentence
          )

          displayOperationResult(result = true)
          sys.exit(0)
      }

      Await.result(Future.never, Duration.Inf)
    }
  }
}
