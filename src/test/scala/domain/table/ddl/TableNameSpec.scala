package domain.table.ddl

import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers

class TableNameSpec extends AsyncFunSpec with Matchers {
  describe("toSqlSentence") {
    it("if the length is less than 64, return as is") {
      val tableName = "a" * 64
      TableName(tableName).toSqlSentence shouldBe tableName
    }

    it(
      "if the length is 64 or more, return the truncated one after 65 truncated"
    ) {
      val tableName = "a" * 65
      TableName(tableName).toSqlSentence shouldBe tableName.substring(0, 64)
    }
  }
}
