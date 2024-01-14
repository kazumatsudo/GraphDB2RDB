package domain.table.dml

import domain.table.ddl.TableName
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RecordKeySpec extends AnyFunSpec with Matchers {
  describe("toSqlSentence") {
    it("success") {
      val tableName = TableName("test_table")
      val recordId = RecordId(1)
      val recordKey = RecordKey(tableName, recordId)

      recordKey.toSqlSentence shouldBe tableName.toSqlSentence
    }
  }
}
