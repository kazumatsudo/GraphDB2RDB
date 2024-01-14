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
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import utils.VertexQuery

class GraphVertexSpec extends AnyFunSpec with Matchers {
  describe("toDdl") {
    it("get Database Column Information") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getList(0, 1).head

      vertex.toDdl shouldBe TableList(
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

  describe("toDml") {
    it("get SQL Sentence") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getList(0, 1).head

      vertex.toDml shouldBe RecordList(
        Map(
          RecordKey((TableName("vertex"), RecordId(1))) -> RecordValue(
            Map(
              "id" -> 1,
              "property_name" -> "marko",
              "property_age" -> 29,
              "label_person" -> true
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
            (TableName("vertex"), RecordId(vertex1.id()))
          ) -> RecordValue(
            Map(("id" -> 0), "label_testVertex1" -> true)
          )
        )
      )
    }
  }
}
