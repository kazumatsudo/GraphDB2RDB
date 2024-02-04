package domain.graph

import domain.table.ddl.attribute.{
  ForeignKey,
  PrimaryKey,
  UniqueIndex,
  UniqueIndexName
}
import domain.table.ddl.column.{
  ColumnLength,
  ColumnList,
  ColumnName,
  ColumnType,
  ColumnTypeDouble,
  ColumnTypeInt
}
import domain.table.ddl.{TableAttributes, TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import infrastructure.EdgeQuery
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.V
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import utils.Config

class GraphEdgeSpec extends AsyncFunSpec with Matchers {
  private val config = Config.default

  describe("toDdl") {
    it("get Database Column Information") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, config)
      val edge = edgeQuery.getList(0, 1)

      edge.map {
        _.headOption.map(_.toDdl) shouldBe Some(
          TableList(
            Map(
              TableName("edge_knows_from_person_to_person") -> (ColumnList(
                Map[ColumnName, ColumnType](
                  ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("id_in_v") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("id_out_v") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("property_weight") -> ColumnTypeDouble(
                    ColumnLength(3)
                  )
                )
              ), TableAttributes(
                PrimaryKey(Set(ColumnName("id"))),
                ForeignKey(
                  Map(
                    ColumnName("id_in_v") -> (TableName(
                      "vertex_person"
                    ), ColumnName("id")),
                    ColumnName("id_out_v") -> (TableName(
                      "vertex_person"
                    ), ColumnName("id"))
                  )
                ),
                UniqueIndex(
                  Map(
                    UniqueIndexName("index_id_in_v_id_out_v") -> Set(
                      ColumnName("id_in_v"),
                      ColumnName("id_out_v")
                    )
                  )
                )
              ))
            )
          )
        )
      }
    }
  }

  describe("toDml") {
    it("get SQL Sentence") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, config)
      val edge = edgeQuery.getList(0, 1)

      edge.map {
        _.headOption.map(_.toDml) shouldBe Some(
          RecordList(
            Map(
              RecordKey(
                (TableName("edge_knows_from_person_to_person"), RecordId(7))
              ) -> RecordValue(
                Map(
                  "id" -> 7,
                  "id_in_v" -> 2,
                  "id_out_v" -> 1,
                  "property_weight" -> 0.5
                )
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

      val graphEdge = GraphEdge(edge, config)
      graphEdge.toDml shouldBe RecordList(
        Map(
          RecordKey(
            (
              TableName("edge_testEdge_from_testVertex1_to_testVertex2"),
              RecordId(14)
            )
          ) -> RecordValue(
            Map("id" -> 14, "id_in_v" -> 13, "id_out_v" -> 0)
          )
        )
      )
    }
  }
}
