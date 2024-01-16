package usecase

import domain.table.ddl.TableList
import domain.table.dml.RecordList
import infrastructure.{EdgeQuery, VertexQuery}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

final case class UsingSpecificKeyListRequestKey(
    key: String,
    value: Seq[Any]
)
final case class UsingSpecificKeyListRequestLabel(
    label: String,
    value: Seq[UsingSpecificKeyListRequestKey]
)
final case class UsingSpecificKeyListRequest(
    value: Seq[UsingSpecificKeyListRequestLabel]
)

/** analyze specific vertices searched by keys
  *
  * pros:
  *   - faster than [[ByExhaustiveSearch]] (enable to search by index)
  * cons:
  *   - required to prepare search condition
  *
  * @param g
  *   [[GraphTraversalSource]]
  */
final case class UsingSpecificKeyList(
    override protected val g: GraphTraversalSource,
    private val value: UsingSpecificKeyListRequest
) extends UsecaseBase {

  override def execute(
      checkUnique: Boolean
  ): (Option[(TableList, RecordList)], Option[(TableList, RecordList)]) = {

    // 1. get vertex by specific key
    val verticesOption = executeWithExceptionHandling({
      val vertexQuery = VertexQuery(g)
      value.value.view
        .flatMap { label =>
          label.value.view.flatMap { keyValue =>
            keyValue.value.flatMap { value =>
              vertexQuery.getListByPropertyKey(label.label, keyValue.key, value)
            }
          }
        }
    })

    verticesOption match {
      case Some(vertices) =>
        // 2. generate vertex SQL
        val vertexSqlOption = executeWithExceptionHandling({
          vertices
            .map(vertex =>
              (
                vertex.toDdl,
                vertex.toDml
              )
            )
            .reduce[(TableList, RecordList)] {
              case (
                    (tableListAccumlator, dmlAccumlator),
                    (tableListCurrentValue, dmlCurrentValue)
                  ) =>
                (
                  tableListAccumlator.merge(tableListCurrentValue),
                  dmlAccumlator.merge(dmlCurrentValue, checkUnique)
                )
            }
        })

        // 3. generate edge SQL
        val edgeSqlOption = executeWithExceptionHandling({
          val edgeQuery = EdgeQuery(g)
          vertices.view
            .flatMap(vertex =>
              (edgeQuery.getInEdgeList(vertex) ++ edgeQuery.getOutEdgeList(
                vertex
              ))
                .map(edge =>
                  (
                    edge.toDdl,
                    edge.toDml
                  )
                )
            )
            .reduce[(TableList, RecordList)] {
              case (
                    (tableListAccumlator, dmlAccumlator),
                    (tableListCurrentValue, dmlCurrentValue)
                  ) =>
                (
                  tableListAccumlator.merge(tableListCurrentValue),
                  dmlAccumlator.merge(dmlCurrentValue, checkUnique)
                )
            }
        })

        (vertexSqlOption, edgeSqlOption)
      case None => (None, None)
    }
  }
}
