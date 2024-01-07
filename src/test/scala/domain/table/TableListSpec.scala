package domain.table

import domain.table.column.{ColumnLength, ColumnList, ColumnName, ColumnTypeInt, ColumnTypeString}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import utils.vertex.{VertexQuery, VertexUtility}

class TableListSpec extends AnyFunSpec with Matchers {
  describe("merge") {
    it("success") {
      // TODO: not use Vertex
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getVerticesList(0, vertexQuery.countAll.toInt)

      val result = vertex
        .map(vertex => VertexUtility.toTableList(vertex))
        .reduce[TableList] { case (accumulator, currentValue) => accumulator.merge(currentValue) }

      result shouldBe TableList(Map(
        TableName("vertex") -> ColumnList(Map(
          ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
          ColumnName("name") -> ColumnTypeString(ColumnLength(6)),
          ColumnName("lang") -> ColumnTypeString(ColumnLength(4)),
          ColumnName("age") -> ColumnTypeInt(ColumnLength(2))
        ))
      ))
    }
  }
}
