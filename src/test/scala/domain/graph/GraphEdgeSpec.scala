package domain.graph

import domain.table.column._
import domain.table.{TableList, TableName}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import utils.EdgeQuery

class GraphEdgeSpec extends AnyFunSpec with Matchers {
  describe("toDdl") {
    it("get Database Column Information") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph)
      val edge = edgeQuery.getList(0, 1).head

      edge.toDdl shouldBe TableList(
        Map(
          TableName("edge") -> ColumnList(
            Map(
              ColumnName("in_v_id") -> ColumnTypeInt(ColumnLength(1)),
              ColumnName("out_v_id") -> ColumnTypeInt(ColumnLength(1)),
              ColumnName("property_weight") -> ColumnTypeDouble(
                ColumnLength(3)
              ),
              ColumnName("label_knows") -> ColumnTypeBoolean
            )
          )
        )
      )
    }
  }

  describe("toDml") {
    it("get SQL Sentence") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph)
      val edge = edgeQuery.getList(0, 1).head

      edge.toDml shouldBe "INSERT INTO edge (in_v_id, out_v_id, property_weight, label_knows) VALUES (2, 1, 0.5, true);"
    }
  }
}
