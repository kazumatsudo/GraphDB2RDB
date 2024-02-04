package domain.graph

import domain.table.ddl.attribute.{ForeignKey, PrimaryKey, UniqueIndex}
import domain.table.ddl.column.{
  ColumnLength,
  ColumnList,
  ColumnName,
  ColumnType,
  ColumnTypeInt,
  ColumnTypeString
}
import domain.table.ddl.{TableAttributes, TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import infrastructure.VertexQuery
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import utils.Config

class GraphVertexSpec extends AsyncFunSpec with Matchers {
  private val config = Config.default

  describe("toDdl") {
    it("get Database Column Information") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph, config)
      val vertex = vertexQuery.getList(0, 1)

      vertex.map {
        _.headOption.map(_.toDdl) shouldBe Some(
          TableList(
            Map(
              TableName("vertex_person") -> (ColumnList(
                Map[ColumnName, ColumnType](
                  ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("property_name") -> ColumnTypeString(
                    ColumnLength(5)
                  ),
                  ColumnName("property_age") -> ColumnTypeInt(ColumnLength(2))
                )
              ), TableAttributes(
                PrimaryKey(Set(ColumnName("id"))),
                ForeignKey(Map()),
                UniqueIndex(Map())
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
      val vertexQuery = VertexQuery(graph, config)
      val vertex = vertexQuery.getList(0, 1)

      vertex.map {
        _.headOption.map(_.toDml) shouldBe Some(
          RecordList(
            Map(
              RecordKey(
                (TableName("vertex_person"), RecordId(1))
              ) -> RecordValue(
                Map(
                  "id" -> 1,
                  "property_name" -> "marko",
                  "property_age" -> 29
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

      val graphVertex = GraphVertex(vertex1, config)
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
