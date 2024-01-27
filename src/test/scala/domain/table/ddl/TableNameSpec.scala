package domain.table.ddl

import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers

class TableNameSpec extends AsyncFunSpec with Matchers {
  describe("toSqlSentence") {
    it("if the length is less than (63 - 7), return as is") {
      val tableName = "a" * (63 - 7)
      TableName(tableName).toSqlSentence shouldBe tableName
    }

    it(
      "if the length is (63 - 7) or more, return the truncated one after (63 - 7) + 1 truncated"
    ) {
      val tableName = "a" * (63 - 7) + 1
      TableName(tableName).toSqlSentence shouldBe tableName.substring(0, 63 - 7)
    }
  }
}
