package domain.table.ddl.attribute

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class UniqueIndexNameSpec extends AnyFunSpec with Matchers {
  describe("toSqlSentence") {
    it("success") {
      val value = "uniqueIndexName"
      UniqueIndexName(value).toSqlSentence shouldBe value
    }
  }
}
