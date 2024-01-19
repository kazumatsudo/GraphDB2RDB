package usecase

import com.typesafe.scalalogging.StrictLogging
import domain.table.ddl.TableList
import domain.table.dml.RecordList
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

final case class UsecaseResponse(
    verticesDdl: Option[TableList],
    verticesDml: Option[RecordList],
    edgesDdl: Option[TableList],
    edgesDml: Option[RecordList]
)

trait UsecaseBase extends StrictLogging {

  protected val g: GraphTraversalSource
  def execute(checkUnique: Boolean): UsecaseResponse
}
