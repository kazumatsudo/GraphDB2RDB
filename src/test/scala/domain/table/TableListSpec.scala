package domain.table

import domain.table.column.{
  ColumnLength,
  ColumnList,
  ColumnName,
  ColumnTypeBoolean,
  ColumnTypeInt,
  ColumnTypeString
}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import utils.vertex.{VertexQuery, VertexUtility}

class TableListSpec extends AnyFunSpec with Matchers {
  describe("merge") {
    it("success") {
      // TODO: not use Vertex
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getVerticesList(0, vertexQuery.countAll.toInt)

      val result = vertex
        .map(vertex => VertexUtility.toTableList(vertex))
        .reduce[TableList] { case (accumulator, currentValue) =>
          accumulator.merge(currentValue)
        }

      result shouldBe TableList(
        Map(
          TableName("vertex") -> ColumnList(
            Map(
              ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
              ColumnName("property_age") -> ColumnTypeInt(ColumnLength(2)),
              ColumnName("property_lang") -> ColumnTypeString(ColumnLength(4)),
              ColumnName("property_name") -> ColumnTypeString(ColumnLength(6)),
              ColumnName("label_person") -> ColumnTypeBoolean,
              ColumnName("label_software") -> ColumnTypeBoolean
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
      val vertex = vertexQuery.getVerticesList(0, vertexQuery.countAll.toInt)

      val vertexAnalyzedResult = vertex
        .map(vertex => VertexUtility.toTableList(vertex))
        .reduce[TableList] { case (accumulator, currentValue) =>
          accumulator.merge(currentValue)
        }

      vertexAnalyzedResult.toSqlSentence shouldBe "CREATE TABLE IF NOT EXISTS vertex (property_age INT(2), property_lang VARCHAR(4), property_name VARCHAR(6), label_software BOOLEAN, id INT(1), label_person BOOLEAN);"
    }
  }
}
