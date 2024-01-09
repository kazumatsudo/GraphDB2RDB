package utils.vertex

import domain.table.{TableList, TableName}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import domain.table.column.{
  ColumnLength,
  ColumnList,
  ColumnName,
  ColumnTypeBoolean,
  ColumnTypeInt,
  ColumnTypeString
}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class VertexUtilitySpec extends AnyFunSpec with Matchers {
  describe("toTableList") {
    it("get Database Column Information") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getVerticesList(0, 1).head

      VertexUtility.toTableList(vertex) shouldBe TableList(
        Map(
          TableName("vertex") -> ColumnList(
            Map(
              ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
              ColumnName("property_name") -> ColumnTypeString(ColumnLength(5)),
              ColumnName("property_age") -> ColumnTypeInt(ColumnLength(2)),
              ColumnName("label_person") -> ColumnTypeBoolean
            )
          )
        )
      )
    }
  }

  describe("toSqlSentence") {
    it("get SQL Sentence") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getVerticesList(0, 1).head

      VertexUtility.toSqlSentence(
        vertex
      ) shouldBe "INSERT INTO vertex (id, property_name, property_age, label_person) VALUES (1, \"marko\", 29, true);"
    }
  }
}
