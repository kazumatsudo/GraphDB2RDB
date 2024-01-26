package usecase

import com.typesafe.scalalogging.StrictLogging
import domain.graph.{GraphEdge, GraphVertex}
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

  protected def fromEdgeToDdl(value: View[GraphEdge]): TableList =
    value.foldLeft(TableList(Map.empty)) { case (accumulator, currentValue) =>
      accumulator.merge(currentValue.toDdl)
    }

  protected def fromEdgeToDml(
      value: View[GraphEdge],
      checkUnique: Boolean
  ): RecordList =
    value.foldLeft(RecordList(Map.empty)) { case (accumulator, currentValue) =>
      accumulator.merge(currentValue.toDml, checkUnique)
    }

  protected def fromVertexToDdl(value: View[GraphVertex]): TableList =
    value.foldLeft(TableList(Map.empty)) { case (accumulator, currentValue) =>
      accumulator.merge(currentValue.toDdl)
    }

  protected def fromVertexToDml(
      value: View[GraphVertex],
      checkUnique: Boolean
  ): RecordList =
    value.foldLeft(RecordList(Map.empty)) { case (accumulator, currentValue) =>
      accumulator.merge(currentValue.toDml, checkUnique)
    }

  def execute(checkUnique: Boolean)(implicit
      ec: ExecutionContext
  ): Future[UsecaseResponse]
}
