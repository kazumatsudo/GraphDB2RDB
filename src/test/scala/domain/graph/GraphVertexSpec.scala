package domain.graph

import domain.table.column._
import domain.table.{TableList, TableName}
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

      vertex.toDml shouldBe "INSERT INTO vertex (id, property_name, property_age, label_person) VALUES (1, \"marko\", 29, true);"
    }

    it("not to write extra comma") {
      val graph = TinkerFactory.createModern().traversal()
      val vertex1 = graph.addV("testVertex1").next()

      val graphVertex = GraphVertex(vertex1)
      graphVertex.toDml shouldBe "INSERT INTO vertex (id, label_testVertex1) VALUES (0, true);"
    }
  }
}
