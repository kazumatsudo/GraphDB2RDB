package usecase

import domain.table.ddl.TableList
import domain.table.dml.RecordList
import infrastructure.{EdgeQuery, VertexQuery}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import java.util.concurrent.Executors.newFixedThreadPool
import scala.collection.View
import scala.collection.parallel.immutable.ParHashMap
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

  override def execute(checkUnique: Boolean): UsecaseResponse = {

    // 1. get vertex by specific key
    val vertexQuery = VertexQuery(g)
    val edgeQuery = EdgeQuery(g)

    def foldLeft(
        value: View[(TableList, RecordList)]
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

    val ((vertexTableList, vertexRecordList), (edgeTableList, edgeRecordList)) =
      Await.result(
        Future
          .sequence {
            {
              for {
                label <- value.value.view
                keyValue <- label.value.view
                value <- keyValue.value.view
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
                        foldLeft(edgeList.map(edge => (edge.toDdl, edge.toDml)))
                      )
                  }
                }
                outEdgesSql <- Future.sequence {
                  vertices.map { vertex =>
                    edgeQuery
                      .getOutEdgeList(vertex)
                      .map(edgeList =>
                        foldLeft(edgeList.map(edge => (edge.toDdl, edge.toDml)))
                      )
                  }
                }
              } yield {
                val verticesSql = {
                  foldLeft(vertices.map(vertex => (vertex.toDdl, vertex.toDml)))
                }
                val edgesSql = foldLeft(inEdgesSql ++ outEdgesSql)
                (verticesSql, edgesSql)
              }
            }
          }
          .map { seq =>
            val (verticesSql, edgesSql) = seq.unzip
            (foldLeft(verticesSql.view), foldLeft(edgesSql.view))
          },
        Duration.Inf
      )

    UsecaseResponse(
      Option(vertexTableList),
      Option(vertexRecordList),
      Option(edgeTableList),
      Option(edgeRecordList)
    )
  }
}
