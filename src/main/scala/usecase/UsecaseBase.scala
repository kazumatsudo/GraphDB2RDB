package usecase

import com.typesafe.scalalogging.StrictLogging
import domain.graph.GraphElement
import domain.table.ddl.TableList
import domain.table.dml.RecordList
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import utils.Config

import scala.collection.View
import scala.concurrent.{ExecutionContext, Future}

final case class UsecaseResponse(
    verticesDdl: TableList,
    verticesDml: RecordList,
    edgesDdl: TableList,
    edgesDml: RecordList
)

trait UsecaseBase extends StrictLogging {

  protected val g: GraphTraversalSource
  protected val config: Config

  protected def toDdl[T <: GraphElement](
      value: View[T]
  )(implicit ec: ExecutionContext): Future[TableList] = Future {
    value.foldLeft(TableList(Map.empty)) { case (accumulator, currentValue) =>
      accumulator.merge(currentValue.toDdl)
    }
  }

  protected def toDml[T <: GraphElement](
      value: View[T],
      checkUnique: Boolean
  )(implicit ec: ExecutionContext): Future[RecordList] = Future {
    value.foldLeft(RecordList(Map.empty)) { case (accumulator, currentValue) =>
      accumulator.merge(currentValue.toDml, checkUnique)
    }
  }

  def execute(checkUnique: Boolean)(implicit
      ec: ExecutionContext
  ): Future[UsecaseResponse]
}
