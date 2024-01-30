package usecase

import domain.graph.{GraphEdge, GraphVertex}
import infrastructure.{EdgeQuery, VertexQuery}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import utils.Config

import scala.collection.mutable
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

    val verticesSet: mutable.Set[GraphVertex] = mutable.Set.empty
    val edgesSet: mutable.Set[GraphEdge] = mutable.Set.empty

    def getGraphByVertex(
        graphVertex: GraphVertex
    )(implicit ec: ExecutionContext): Future[Unit] = for {
      inEdges <- edgeQuery.getInEdgeList(graphVertex)
      needToTraverseInEdges = inEdges.filterNot(edgesSet.contains)
      outVertices <- Future.sequence {
        needToTraverseInEdges.map { edge => vertexQuery.getOutVertexList(edge) }
      }

      outEdges <- edgeQuery.getOutEdgeList(graphVertex)
      needToTraverseOutEdges = outEdges.filterNot(edgesSet.contains)
      inVertices <- Future.sequence {
        needToTraverseOutEdges.map { edge => vertexQuery.getInVertexList(edge) }
      }

      needToTraverseVertices = (outVertices ++ inVertices).flatten
        .filterNot(verticesSet.contains)
    } yield {
      verticesSet ++= needToTraverseVertices
      edgesSet ++= needToTraverseInEdges
      edgesSet ++= needToTraverseOutEdges

      Future.sequence { needToTraverseVertices.map(getGraphByVertex) }
    }

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
        } yield Future.sequence { vertices.map(getGraphByVertex) }
      }
      .flatMap { _ =>
        val vertices = verticesSet.view
        val edges = edgesSet.view

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
