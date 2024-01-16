package usecase

import com.typesafe.scalalogging.StrictLogging
import domain.table.ddl.TableList
import domain.table.dml.RecordList
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import scala.util.control.NonFatal

trait UsecaseBase extends StrictLogging {

  protected val g: GraphTraversalSource

  protected def executeWithExceptionHandling[T](
      function: => T
  ): Option[T] = {
    try {
      Some(function)
    } catch {
      case NonFatal(_) => None
    }
  }

  def execute(
      checkUnique: Boolean
  ): (
      Option[TableList],
      Option[RecordList],
      Option[TableList],
      Option[RecordList]
  )
}
