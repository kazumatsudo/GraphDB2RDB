package usecase

import domain.table.ddl.TableList
import domain.table.dml.RecordList
import infrastructure.{EdgeQuery, VertexQuery}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import utils.Config

import scala.concurrent.{ExecutionContext, Future}

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
  * @param config
  *   [[Config]]
  */
final case class UsingSpecificKeyList(
    override protected val g: GraphTraversalSource,
    override protected val config: Config,
    private val value: UsingSpecificKeyListRequest
) extends UsecaseBase {

  override def execute(
      checkUnique: Boolean
  )(implicit ec: ExecutionContext): Future[UsecaseResponse] = Future {

    // 1. get vertex by specific key
    val vertices = {
      val vertexQuery = VertexQuery(g, config)
      value.value.view
        .flatMap { label =>
          label.value.view.flatMap { keyValue =>
            keyValue.value.flatMap { value =>
              vertexQuery.getListByPropertyKey(label.label, keyValue.key, value)
            }
          }
        }
    }

    // 2. generate vertex SQL
    val edgeQuery = EdgeQuery(g, config)

    val (verticesDdl, verticesDml, edgesDdl, edgesDml) = {
      vertices
        .map { vertex =>
          val (edgesDdl, edgesDml) =
            (edgeQuery.getInEdgeList(vertex) ++ edgeQuery
              .getOutEdgeList(vertex)).view
              .map(edge => (edge.toDdl, edge.toDml))
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

          (
            vertex.toDdl,
            vertex.toDml,
            edgesDdl,
            edgesDml
          )
        }
        .reduce[(TableList, RecordList, TableList, RecordList)] {
          case (
                (
                  vertexDdlAccumlator,
                  vertexDmlAccumlator,
                  edgeDdlAccumlator,
                  edgeDmlAccumlator
                ),
                (
                  vertexDdlCurrentValue,
                  vertexDmlCurrentValue,
                  edgeDdlCurrentValue,
                  edgeDmlCurrentValue
                )
              ) =>
            (
              vertexDdlAccumlator.merge(vertexDdlCurrentValue),
              vertexDmlAccumlator.merge(vertexDmlCurrentValue, checkUnique),
              edgeDdlAccumlator.merge(edgeDdlCurrentValue),
              edgeDmlAccumlator.merge(edgeDmlCurrentValue, checkUnique)
            )
        }
    }

    UsecaseResponse(
      verticesDdl,
      verticesDml,
      edgesDdl,
      edgesDml
    )
  }
}
