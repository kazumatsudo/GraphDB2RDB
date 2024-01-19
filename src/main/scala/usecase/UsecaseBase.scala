package usecase

import com.typesafe.scalalogging.StrictLogging
import domain.table.ddl.TableList
import domain.table.dml.RecordList
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import scala.collection.View
import scala.collection.parallel.immutable.ParHashMap
import scala.concurrent.{ExecutionContext, Future}

final case class UsecaseResponse(
    verticesDdl: TableList,
    verticesDml: RecordList,
    edgesDdl: TableList,
    edgesDml: RecordList
)

trait UsecaseBase extends StrictLogging {

  protected val g: GraphTraversalSource

  protected def foldLeft(
      value: View[(TableList, RecordList)],
      checkUnique: Boolean
  ): (TableList, RecordList) = {
    value.foldLeft(
      (TableList(ParHashMap.empty), RecordList(ParHashMap.empty))
    ) {
      case (
            (ddlAccumlator, dmlAccumlator),
            (ddlCurrentValue, dmlCurrentValue)
          ) =>
        (
          ddlAccumlator.merge(ddlCurrentValue),
          dmlAccumlator.merge(dmlCurrentValue, checkUnique)
        )
    }
  }
  def execute(checkUnique: Boolean)(implicit
      ec: ExecutionContext
  ): Future[UsecaseResponse]
}
