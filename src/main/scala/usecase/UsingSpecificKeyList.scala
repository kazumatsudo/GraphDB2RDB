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

    def getGraphByVertex(
        graphVertex: GraphVertex,
        verticesSet: Set[GraphVertex],
        edgesSet: Set[GraphEdge]
    )(implicit
        ec: ExecutionContext
    ): Future[(Set[GraphVertex], Set[GraphEdge])] = for {
      // inEdges
      inEdges <- edgeQuery.getInEdgeList(graphVertex)
      needToTraverseInEdges = inEdges.filterNot(edgesSet.contains)

      // outEdges
      outEdges <- edgeQuery.getOutEdgeList(graphVertex)
      needToTraverseOutEdges = outEdges.filterNot(edgesSet.contains)

      // add edges
      addedEdges = edgesSet ++ needToTraverseInEdges ++ needToTraverseOutEdges

      // outVertices
      outVertices <- Future.sequence {
        needToTraverseInEdges.map { vertexQuery.getOutVertexList }
      }
      needToTraverseOutVertices = outVertices.flatten
        .filterNot(verticesSet.contains)

      // inVertices
      inVertices <- Future.sequence {
        needToTraverseOutEdges.map { vertexQuery.getInVertexList }
      }
      needToTraverseInVertices = inVertices.flatten
        .filterNot(verticesSet.contains)

      // add vertices
      addedVertices = verticesSet ++ outVertices.flatten ++ inVertices.flatten

      // traverse outVertices
      outVerticesResult <- Future.sequence {
        needToTraverseOutVertices.map(
          getGraphByVertex(_, addedVertices, addedEdges)
        )
      }
      (outVerticesResultVertex, outVerticesResultEdges) =
        outVerticesResult.unzip

      // traverse inVertices
      inVerticesResult <- Future.sequence {
        needToTraverseInVertices.map(
          getGraphByVertex(
            _,
            addedVertices ++ outVerticesResultVertex.flatten,
            addedEdges ++ outVerticesResultEdges.flatten
          )
        )
      }
      (inVerticesResultVertex, inVerticesResultEdges) = inVerticesResult.unzip
    } yield {
      (
        addedVertices ++ inVerticesResultVertex.flatten,
        addedEdges ++ inVerticesResultEdges.flatten
      )
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
          result <- Future.sequence {
            vertices.map(getGraphByVertex(_, Set.empty, Set.empty))
          }
        } yield result
      }
      .flatMap { result =>
        val (verticesSet, edgesSet) = result.flatten.unzip
        val vertices = verticesSet.flatten
        val edges = edgesSet.flatten

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
