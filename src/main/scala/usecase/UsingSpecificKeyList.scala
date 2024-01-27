package usecase

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
  )(implicit ec: ExecutionContext): Future[UsecaseResponse] = {

    // 1. get vertex by specific key
    val vertexQuery = VertexQuery(g, config)
    val edgeQuery = EdgeQuery(g, config)

    Future
      .sequence {
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
          inEdges <- Future.sequence {
            vertices.map(edgeQuery.getInEdgeList(_))
          }
          outEdges <- Future.sequence {
            vertices.map(edgeQuery.getOutEdgeList(_))
          }
        } yield (vertices, inEdges, outEdges)
      }
      .flatMap { result =>
        val (verticesResult, inEdgesResult, outEdgesResult) = result.unzip3
        val vertices = verticesResult.flatten
        val edges = (inEdgesResult ++ outEdgesResult).flatten.flatten

        for {
          vertexTableList <- toDdl(vertices, checkUnique)
          vertexRecordList <- toDml(vertices, checkUnique)
          edgeTableList <- toDdl(edges, checkUnique)
          edgeRecordList <- toDml(edges, checkUnique)
        } yield UsecaseResponse(
          vertexTableList,
          vertexRecordList,
          edgeTableList,
          edgeRecordList
        )
      }
  }
}
