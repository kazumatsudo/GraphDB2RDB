package utils.edge

import domain.table.{TableList, TableName}
import domain.table.column.{ColumnLength, ColumnList, ColumnName, ColumnTypeDouble, ColumnTypeInt}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EdgeUtilitySpec extends AnyFunSpec with Matchers {
  describe("toTableList") {
    it("get Database Column Information") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph)
      val edge = edgeQuery.getEdgesList(0, 1).head

      EdgeUtility.toTableList(edge) shouldBe TableList(Map(
        TableName("edge") -> ColumnList(Map(
          ColumnName("in_v_id") -> ColumnTypeInt(ColumnLength(1)),
          ColumnName("out_v_id") -> ColumnTypeInt(ColumnLength(1)),
          ColumnName("weight") -> ColumnTypeDouble(ColumnLength(3))
        ))
      ))
    }
  }

  describe("toSqlSentence") {
    it("get SQL Sentence") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph)
      val edge = edgeQuery.getEdgesList(0, 1).head

      EdgeUtility.toSqlSentence(edge) shouldBe "INSERT INTO edge (in_v_id, out_v_id, weight) VALUES (2, 1, 0.5);"
    }
  }
}