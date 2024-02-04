package usecase

import domain.graph.{GraphEdge, GraphVertex}
import infrastructure.{EdgeQuery, VertexQuery}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import utils.Config

import scala.collection.{View, mutable}
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

    @SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
    val verticesSet = mutable.Set.empty[GraphVertex]
    @SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
    val edgesSet = mutable.Set.empty[GraphEdge]

    def getEdges(graphVertex: GraphVertex) = for {
      // inEdges
      inEdges <- edgeQuery.getInEdgeList(graphVertex)
      needToTraverseInEdges = inEdges.filterNot(edgesSet.contains).toIndexedSeq

      // outEdges
      outEdges <- edgeQuery.getOutEdgeList(graphVertex)
      needToTraverseOutEdges = outEdges
        .filterNot(edgesSet.contains)
        .toIndexedSeq
    } yield {
      edgesSet ++= needToTraverseInEdges
      edgesSet ++= needToTraverseOutEdges

      (needToTraverseInEdges, needToTraverseOutEdges)
    }

    def getVertices(
        inEdges: IndexedSeq[GraphEdge],
        outEdges: IndexedSeq[GraphEdge]
    ) = for {
      // outVertices
      outVertices <- Future.sequence {
        inEdges.map {
          vertexQuery.getOutVertexList
        }
      }
      needToTraverseOutVertices = outVertices.flatten
        .filterNot(verticesSet.contains)

      // inVertices
      inVertices <- Future.sequence {
        outEdges.map {
          vertexQuery.getInVertexList
        }
      }
      needToTraverseInVertices = inVertices.flatten
        .filterNot(verticesSet.contains)
    } yield {
      verticesSet ++= needToTraverseInVertices
      verticesSet ++= needToTraverseOutVertices

      (needToTraverseInVertices, needToTraverseOutVertices)
    }

    def getGraphByVertex(graphVertex: GraphVertex)(implicit
        ec: ExecutionContext
    ): Future[Boolean] = {
      for {
        // add edges if need
        (needToTraverseInEdges, needToTraverseOutEdges) <- getEdges(graphVertex)
        needToTraverseEdges = (needToTraverseInEdges ++ needToTraverseOutEdges)
        result <-
          if (needToTraverseEdges.isEmpty) {
            Future.successful(true)
          } else {
            for {
              (needToTraverseInVertices, needToTraverseOutVertices) <-
                getVertices(needToTraverseInEdges, needToTraverseOutEdges)
              needToTraverseVertices =
                needToTraverseInVertices ++ needToTraverseOutVertices
              result <-
                if (needToTraverseVertices.isEmpty) {
                  Future.successful(true)
                } else {
                  Future
                    .sequence { needToTraverseVertices.map(getGraphByVertex) }
                    .map(_.forall(identity))
                }
            } yield result
          }
      } yield result
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
          _ = verticesSet ++= vertices
          result <- Future.sequence { vertices.map(getGraphByVertex) }
        } yield result
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
