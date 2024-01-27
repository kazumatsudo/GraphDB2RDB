package domain.table.ddl

import domain.table.ddl.attribute.PrimaryKey
import domain.table.ddl.column.ColumnName
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TableAttributesSpec extends AnyFunSpec with Matchers {
  describe("merge") {
    it("success") {
      val primaryKey = PrimaryKey(Set(ColumnName("test1")))
      val tableAttributes = TableAttributes(primaryKey)
      val target = TableAttributes(primaryKey)

      tableAttributes.merge(target) shouldBe tableAttributes
    }
  }

  describe("toSqlSentenceSeq") {
    it("success") {
      val primaryKey = PrimaryKey(Set(ColumnName("test1")))
      val tableAttributes = TableAttributes(primaryKey)

      tableAttributes.toSqlSentenceSeq shouldBe List("PRIMARY KEY (test1)")
    }
  }
}
