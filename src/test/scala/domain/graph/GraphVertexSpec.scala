package domain.graph

import domain.table.ddl.column.{
  ColumnLength,
  ColumnList,
  ColumnName,
  ColumnTypeBoolean,
  ColumnTypeInt,
  ColumnTypeString
}
import domain.table.ddl.{TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import infrastructure.VertexQuery
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GraphVertexSpec extends AnyFunSpec with Matchers {
  describe("toDdl") {
    it("get Database Column Information") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getList(0, 1).head

      vertex.toDdl shouldBe TableList(
        Map(
          TableName("vertex_person") -> ColumnList(
            Map(
              ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
              ColumnName("property_age") -> ColumnTypeInt(ColumnLength(2)),
              ColumnName("property_name") -> ColumnTypeString(ColumnLength(5))
            )
          )
        )
      )
    }
  }

  describe("toDml") {
    it("get SQL Sentence") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getList(0, 1).head

      vertex.toDml shouldBe RecordList(
        Map(
          RecordKey((TableName("vertex_person"), RecordId(1))) -> RecordValue(
            Map(
              "id" -> 1,
              "property_name" -> "marko",
              "property_age" -> 29
            )
          )
        )
      )
    }

    it("not to write extra comma") {
      val graph = TinkerFactory.createModern().traversal()
      val vertex1 = graph.addV("testVertex1").next()

      val graphVertex = GraphVertex(vertex1)
      graphVertex.toDml shouldBe RecordList(
        Map(
          RecordKey(
            (TableName("vertex_testVertex1"), RecordId(vertex1.id()))
          ) -> RecordValue(
            Map("id" -> 0)
          )
        )
      )
    }
  }
}
