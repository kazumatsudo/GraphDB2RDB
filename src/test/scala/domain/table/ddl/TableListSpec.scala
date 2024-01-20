package domain.table.ddl

import domain.table.ddl.column._
import infrastructure.VertexQuery
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import utils.Config

class TableListSpec extends AnyFunSpec with Matchers {
  private val config = Config.default

  describe("merge") {
    it("success") {
      // TODO: not use Vertex
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph, config)
      val vertex = vertexQuery.getList(0, vertexQuery.countAll.toInt)

      val result = vertex
        .map(_.toDdl)
        .reduce[TableList] { case (accumulator, currentValue) =>
          accumulator.merge(currentValue)
        }

      result shouldBe TableList(
        Map(
          TableName("vertex_person") -> ColumnList(
            Map(
              ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
              ColumnName("property_age") -> ColumnTypeInt(ColumnLength(2)),
              ColumnName("property_name") -> ColumnTypeString(ColumnLength(5))
            )
          ),
          TableName("vertex_software") -> ColumnList(
            Map(
              ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
              ColumnName("property_lang") -> ColumnTypeString(ColumnLength(4)),
              ColumnName("property_name") -> ColumnTypeString(ColumnLength(6))
            )
          )
        )
      )
    }
  }

  describe("toSqlSentence") {
    it("success") {
      // TODO: not use Vertex
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph, config)
      val vertex = vertexQuery.getList(0, vertexQuery.countAll.toInt)

      val vertexAnalyzedResult = vertex
        .map(_.toDdl)
        .reduce[TableList] { case (accumulator, currentValue) =>
          accumulator.merge(currentValue)
        }

      vertexAnalyzedResult.toSqlSentence.toSeq shouldBe Seq(
        "CREATE TABLE IF NOT EXISTS vertex_person (id INT, property_age INT, property_name VARCHAR(5));",
        "CREATE TABLE IF NOT EXISTS vertex_software (id INT, property_lang VARCHAR(4), property_name VARCHAR(6));"
      )
    }
  }
}
