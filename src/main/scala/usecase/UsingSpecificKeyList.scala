package usecase

import domain.table.ddl.TableList
import domain.table.dml.RecordList
import infrastructure.{EdgeQuery, VertexQuery}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

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

  implicit private val ec: ExecutionContext = ExecutionContext.Implicits.global

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

    def r(
        value: Seq[(TableList, RecordList, TableList, RecordList)]
    ): (TableList, RecordList, TableList, RecordList) = {
      value.reduce[(TableList, RecordList, TableList, RecordList)] {
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
            vertexDmlAccumlator
              .merge(vertexDmlCurrentValue, checkUnique),
            edgeDdlAccumlator.merge(edgeDdlCurrentValue),
            edgeDmlAccumlator.merge(edgeDmlCurrentValue, checkUnique)
          )
      }
    }

    val (vertexTableList, vertexRecordList, edgeTableList, edgeRecordList) =
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
                inEdgesSeq <- Future.sequence(
                  vertices.map(edgeQuery.getInEdgeList)
                )
                outEdgesSeq <- Future.sequence(
                  vertices.map(edgeQuery.getOutEdgeList)
                )
              } yield for {
                vertex <- vertices
                edge <- inEdgesSeq.flatten ++ outEdgesSeq.flatten
              } yield (vertex.toDdl, vertex.toDml, edge.toDdl, edge.toDml)
            }.map(_.map(r))
          }
          .map(r),
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
