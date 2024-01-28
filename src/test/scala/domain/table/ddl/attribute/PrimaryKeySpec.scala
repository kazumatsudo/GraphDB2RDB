package domain.table.ddl.attribute

import domain.table.ddl.column.ColumnName
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PrimaryKeySpec extends AnyFunSpec with Matchers {
  describe("merge") {
    it("success when the two values are the same.") {
      val value = Set(ColumnName("test1"), ColumnName("test2"))
      val primaryKey = PrimaryKey(value)
      primaryKey.merge(primaryKey) shouldBe primaryKey
    }

    it("fail when the two values are not the same.") {
      val value1 = Set(ColumnName("test1"), ColumnName("test2"))
      val primaryKey1 = PrimaryKey(value1)
      val value2 = Set(ColumnName("test1"), ColumnName("test3"))
      val primaryKey2 = PrimaryKey(value2)

      intercept[IllegalArgumentException] {
        primaryKey1.merge(primaryKey2)
      }
    }
  }

  describe("toSqlSentence") {
    it("generate SQL Sentence") {
      val value = Set(ColumnName("test1"), ColumnName("test2"))
      val primaryKey = PrimaryKey(value)
      primaryKey.toSqlSentence shouldBe "PRIMARY KEY (test1, test2)"
    }
  }
}
