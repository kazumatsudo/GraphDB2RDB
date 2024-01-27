package domain.table.ddl.column

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ColumnLengthSpec extends AnyFunSpec with Matchers {
  describe("needToUseMediumText") {
    it("return true if the length is too long (2^16 or more)") {
      ColumnLength(math.pow(2, 16).toInt).needToUseMediumText shouldBe true
    }

    it("return false if the length is too long (2^16 - 1 or less)") {
      ColumnLength(math.pow(2, 16).toInt - 1).needToUseMediumText shouldBe false
    }
  }

  describe("max") {
    it("return the larger number of length") {
      ColumnLength(1).max(ColumnLength(2)) shouldBe ColumnLength(2)
    }
  }

  describe("toSqlSentence") {
    it("return the value for SQL") {
      ColumnLength(1).toSqlSentence shouldBe "1"
    }
  }
}
