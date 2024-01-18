package usecase

import domain.table.ddl.TableList
import domain.table.dml.RecordList
import infrastructure.{EdgeQuery, VertexQuery}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import java.util.concurrent.Executors.newFixedThreadPool
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

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

  // set gremlin server connection pool max size or less
  implicit private val ec: ExecutionContext =
    ExecutionContext.fromExecutor(newFixedThreadPool(1))

  override def execute(
      checkUnique: Boolean
  ): (
      Option[TableList],
      Option[RecordList],
      Option[TableList],
      Option[RecordList]
  ) = {

    // 1. get vertex by specific key
    val vertexQuery = VertexQuery(g)
    val edgeQuery = EdgeQuery(g)

    def reduce(value: Seq[(TableList, RecordList)]): (TableList, RecordList) = {
      value.reduce[(TableList, RecordList)] {
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

    val ((vertexTableList, vertexRecordList), (edgeTableList, edgeRecordList)) =
      Await.result(
        Future
          .sequence {
            {
              for {
                label <- value.value
                keyValue <- label.value
                value <- keyValue.value
              } yield for {
                vertices <- vertexQuery.getListByPropertyKey(
                  label.label,
                  keyValue.key,
                  value
                )
                inEdgesSql <- Future.sequence {
                  vertices.map { vertex =>
                    edgeQuery
                      .getInEdgeList(vertex)
                      .map(edgeList =>
                        reduce(edgeList.map(edge => (edge.toDdl, edge.toDml)))
                      )
                  }
                }
                outEdgesSql <- Future.sequence {
                  vertices.map { vertex =>
                    edgeQuery
                      .getOutEdgeList(vertex)
                      .map(edgeList =>
                        reduce(edgeList.map(edge => (edge.toDdl, edge.toDml)))
                      )
                  }
                }
              } yield {
                val verticesSql =
                  reduce(vertices.map(vertex => (vertex.toDdl, vertex.toDml)))
                val edgesSql = reduce(inEdgesSql ++ outEdgesSql)
                (verticesSql, edgesSql)
              }
            }
          }
          .map { seq =>
            val (verticesSql, edgesSql) = seq.unzip
            (reduce(verticesSql), reduce(edgesSql))
          },
        Duration.Inf
      )

    (
      Option(vertexTableList),
      Option(vertexRecordList),
      Option(edgeTableList),
      Option(edgeRecordList)
    )
  }
}
