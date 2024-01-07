package utils.vertex

import domain.table.{TableList, TableName}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import domain.table.column.{ColumnLength, ColumnList, ColumnName, ColumnTypeInt, ColumnTypeString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class VertexUtilitySpec extends AnyFunSpec with Matchers {
  describe("toTableList") {
    it("get Database Column Information") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getVerticesList(0, 1).head

      VertexUtility.toTableList(vertex) shouldBe TableList(Map(
        TableName("vertex") -> ColumnList(Map(
          ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
          ColumnName("name") -> ColumnTypeString(ColumnLength(5)),
          ColumnName("age") -> ColumnTypeInt(ColumnLength(2))
        ))
      ))
    }
  }
}
