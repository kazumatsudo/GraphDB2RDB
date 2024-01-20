package domain.table.dml

import domain.table.ddl.TableName
import infrastructure.VertexQuery
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RecordListSpec extends AnyFunSpec with Matchers {
  describe("merge") {
    describe("success") {
      it("if RecordList has no value") {
        val tableName = TableName("test_table")
        val recordId = RecordId(1)
        val recordKey = RecordKey(tableName, recordId)
        val recordValue = RecordValue(Map(("boolean", false)))
        val recordList = RecordList(Map((recordKey, recordValue)))

        RecordList(Map.empty)
          .merge(recordList, checkUnique = true) shouldBe recordList
      }

      it("if the target has no value") {
        val tableName = TableName("test_table")
        val recordId = RecordId(1)
        val recordKey = RecordKey(tableName, recordId)
        val recordValue = RecordValue(Map(("boolean", false)))
        val recordList = RecordList(Map((recordKey, recordValue)))

        recordList.merge(
          RecordList(Map.empty),
          checkUnique = true
        ) shouldBe recordList
      }

      it("if RecordList has the same value") {
        val tableName = TableName("test_table")
        val recordId = RecordId(1)
        val recordKey = RecordKey(tableName, recordId)
        val recordValue = RecordValue(Map(("boolean", false)))
        val recordList = RecordList(Map((recordKey, recordValue)))

        recordList.merge(recordList, checkUnique = true) shouldBe recordList
      }

      it("if checkUnique is false, update after the value") {
        val tableName = TableName("test_table")
        val recordId = RecordId(1)
        val recordKey = RecordKey(tableName, recordId)

        val recordValue1 = RecordValue(Map(("boolean", false)))
        val recordList1 = RecordList(Map((recordKey, recordValue1)))
        val recordValue2 = RecordValue(Map(("int", 1)))
        val recordList2 = RecordList(Map((recordKey, recordValue2)))

        recordList1.merge(recordList2, checkUnique = false) shouldBe recordList2
      }
    }

    describe("failure") {
      it("if RecordList has the same key but the values are not the same") {
        val tableName = TableName("test_table")
        val recordId = RecordId(1)
        val recordKey = RecordKey(tableName, recordId)

        val recordValue1 = RecordValue(Map(("boolean", false)))
        val recordList1 = RecordList(Map((recordKey, recordValue1)))
        val recordValue2 = RecordValue(Map(("int", 1)))
        val recordList2 = RecordList(Map((recordKey, recordValue2)))

        intercept[IllegalArgumentException] {
          recordList1.merge(recordList2, checkUnique = true)
        }
      }
    }
  }

  describe("toSqlSentence") {
    it("success") {
      // TODO: not use Vertex
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      val vertex = vertexQuery.getList(0, vertexQuery.countAll.toInt)

      val vertexAnalyzedResult = vertex
        .map(_.toDml)
        .reduce[RecordList] { case (accumulator, currentValue) =>
          accumulator.merge(currentValue, checkUnique = false)
        }

      vertexAnalyzedResult.toSqlSentence.toSeq shouldBe Seq(
        "INSERT INTO vertex_software (id, property_lang, property_name) VALUES (5, 'java', 'ripple');",
        "INSERT INTO vertex_person (id, property_age, property_name) VALUES (1, 29, 'marko');",
        "INSERT INTO vertex_software (id, property_lang, property_name) VALUES (3, 'java', 'lop');",
        "INSERT INTO vertex_person (id, property_age, property_name) VALUES (2, 27, 'vadas');",
        "INSERT INTO vertex_person (id, property_age, property_name) VALUES (6, 35, 'peter');",
        "INSERT INTO vertex_person (id, property_age, property_name) VALUES (4, 32, 'josh');"
      )
    }
  }
}
