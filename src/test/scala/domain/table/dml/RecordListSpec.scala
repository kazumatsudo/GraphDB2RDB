package domain.table.dml

import domain.table.ddl.TableName
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
      RecordValue(
        Map(("boolean", false))
      ).toSqlSentence shouldBe ("boolean", "false")
      RecordValue(Map(("byte", 1.toByte))).toSqlSentence shouldBe ("byte", "1")
      RecordValue(Map(("int", 1))).toSqlSentence shouldBe ("int", "1")
      RecordValue(Map(("long", 1.toLong))).toSqlSentence shouldBe ("long", "1")
      RecordValue(
        Map(("double", 1.toDouble))
      ).toSqlSentence shouldBe ("double", "1.0")
      RecordValue(
        Map(("char", 'a'))
      ).toSqlSentence shouldBe ("char", "\"a\"")
      RecordValue(
        Map(("string", 1.toString))
      ).toSqlSentence shouldBe ("string", "\"1\"")
      RecordValue(
        Map(("unknown", Seq(1)))
      ).toSqlSentence shouldBe ("unknown", "\"List(1)\"")
    }
  }
}
