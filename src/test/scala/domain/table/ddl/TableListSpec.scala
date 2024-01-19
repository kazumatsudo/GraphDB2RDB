package domain.table.ddl

import domain.table.ddl.column._
import infrastructure.VertexQuery
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.parallel.immutable.ParMap

class TableListSpec extends AnyFunSpec with Matchers {
  describe("merge") {
    it("success") {
      // TODO: not use Vertex
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getList(0, vertexQuery.countAll.toInt)

      val result = vertex
        .map(_.toDdl)
        .reduce[TableList] { case (accumulator, currentValue) =>
          accumulator.merge(currentValue)
        }

      result shouldBe TableList(
        ParMap(
          TableName("vertex_person") -> ColumnList(
            ParMap(
              ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
              ColumnName("property_age") -> ColumnTypeInt(ColumnLength(2)),
              ColumnName("property_name") -> ColumnTypeString(ColumnLength(5))
            )
          ),
          TableName("vertex_software") -> ColumnList(
            ParMap(
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
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getList(0, vertexQuery.countAll.toInt)

      val vertexAnalyzedResult = vertex
        .map(_.toDdl)
        .reduce[TableList] { case (accumulator, currentValue) =>
          accumulator.merge(currentValue)
        }

      vertexAnalyzedResult.toSqlSentence.toSeq shouldBe Seq(
        "CREATE TABLE IF NOT EXISTS vertex_person (id INT(1), property_age INT(2), property_name VARCHAR(5));",
        "CREATE TABLE IF NOT EXISTS vertex_software (id INT(1), property_lang VARCHAR(4), property_name VARCHAR(6));"
      )
    }
  }
}
