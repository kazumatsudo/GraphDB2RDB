package usecase

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

  override def execute(checkUnique: Boolean): UsecaseResponse = {

    // 1. get vertex by specific key
    val vertexQuery = VertexQuery(g)
    val edgeQuery = EdgeQuery(g)

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
                        foldLeft(
                          edgeList.map(edge => (edge.toDdl, edge.toDml)),
                          checkUnique
                        )
                      )
                  }
                }
                outEdgesSql <- Future.sequence {
                  vertices.map { vertex =>
                    edgeQuery
                      .getOutEdgeList(vertex)
                      .map(edgeList =>
                        foldLeft(
                          edgeList.map(edge => (edge.toDdl, edge.toDml)),
                          checkUnique
                        )
                      )
                  }
                }
              } yield {
                val verticesSql = {
                  foldLeft(
                    vertices.map(vertex => (vertex.toDdl, vertex.toDml)),
                    checkUnique
                  )
                }
                val edgesSql =
                  foldLeft((inEdgesSql ++ outEdgesSql), checkUnique)
                (verticesSql, edgesSql)
              }
            }
          }
          .map { seq =>
            val (verticesSql, edgesSql) = seq.unzip
            (
              foldLeft(verticesSql.view, checkUnique),
              foldLeft(edgesSql.view, checkUnique)
            )
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
