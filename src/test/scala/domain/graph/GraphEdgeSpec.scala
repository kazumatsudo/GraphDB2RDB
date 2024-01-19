package domain.graph

import domain.table.ddl.column.{
  ColumnLength,
  ColumnList,
  ColumnName,
  ColumnTypeDouble,
  ColumnTypeInt
}
import domain.table.ddl.{TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import infrastructure.EdgeQuery
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.V
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import utils.Config

import scala.collection.parallel.immutable.ParMap

class GraphEdgeSpec extends AsyncFunSpec with Matchers {
  describe("toDdl") {
    it("get Database Column Information") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, Config.default)
      val edge = edgeQuery.getList(0, 1)

      edge.map { result =>
        result.head.toDdl shouldBe TableList(
          ParMap(
            TableName("edge_knows") -> ColumnList(
              ParMap(
                ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("id_in_v") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("id_out_v") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("property_weight") -> ColumnTypeDouble(
                  ColumnLength(3)
                )
              )
            )
          )
        )
      }
    }
  }

  describe("toDml") {
    it("get SQL Sentence") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, Config.default)
      val edge = edgeQuery.getList(0, 1)

      edge.map { result =>
        result.head.toDml shouldBe RecordList(
          ParMap(
            RecordKey((TableName("edge_knows"), RecordId(7))) -> RecordValue(
              ParMap(
                "id" -> 7,
                "id_in_v" -> 2,
                "id_out_v" -> 1,
                "property_weight" -> 0.5
              )
            )
          )
        )
      }
    }

    it("not to write extra comma") {
      val graph = TinkerFactory.createModern().traversal()
      val vertex1 = graph.addV("testVertex1").next()
      val vertex2 = graph.addV("testVertex2").next()
      val edge = graph.V(vertex1).addE("testEdge").to(V(vertex2)).next()

      val graphEdge = GraphEdge(edge, Config.default)
      graphEdge.toDml shouldBe RecordList(
        ParMap(
          RecordKey((TableName("edge_testEdge"), RecordId(14))) -> RecordValue(
            ParMap("id" -> 14, "id_in_v" -> 13, "id_out_v" -> 0)
          )
        )
      )
    }
  }
}
