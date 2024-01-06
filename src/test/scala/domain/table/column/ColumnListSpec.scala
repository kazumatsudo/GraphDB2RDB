package domain.table.column

import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import utils.{VertexQuery, VertexUtility}

class ColumnListSpec extends AnyFunSpec with Matchers {

  describe("merge") {
    it("success") {
      // TODO: not use Vertex
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getVerticesList(0, vertexQuery.countAll.toInt)

      val result = vertex
        .map(vertex => VertexUtility.toColumnList(vertex))
        .reduce[ColumnList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }

      result shouldBe ColumnList(Map(
        ColumnName("name") -> ColumnTypeString(ColumnLength(6)),
        ColumnName("lang") -> ColumnTypeString(ColumnLength(4)),
        ColumnName("age") -> ColumnTypeInt(ColumnLength(2))
      ))
    }
  }
}
