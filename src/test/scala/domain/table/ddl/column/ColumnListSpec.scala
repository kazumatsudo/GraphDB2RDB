package domain.table.ddl.column

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.parallel.immutable.ParMap

class ColumnListSpec extends AnyFunSpec with Matchers {

  private val columnList1 = ColumnList(
    ParMap(
      ColumnName("id") -> ColumnTypeInt(ColumnLength(11)),
      ColumnName("name") -> ColumnTypeString(ColumnLength(30)),
      ColumnName("address") -> ColumnTypeString(ColumnLength(255))
    )
  )
  private val columnList2 = ColumnList(
    ParMap(
      ColumnName("id") -> ColumnTypeInt(ColumnLength(11)),
      ColumnName("name") -> ColumnTypeString(ColumnLength(50)),
      ColumnName("created_at") -> ColumnTypeUnknown,
      ColumnName("updated_at") -> ColumnTypeUnknown
    )
  )

  describe("merge") {
    it("succeeds when both lists are empty") {
      ColumnList(ParMap.empty).merge(ColumnList(ParMap.empty)) shouldBe ColumnList(ParMap.empty)
    }

    it("succeeds when one list is empty") {
      columnList1.merge(ColumnList(ParMap.empty)) shouldBe columnList1
      ColumnList(ParMap.empty).merge(columnList1) shouldBe columnList1
    }

    it("success") {
      columnList1.merge(columnList2) shouldBe ColumnList(
        ParMap(
          ColumnName("id") -> ColumnTypeInt(ColumnLength(11)),
          ColumnName("name") -> ColumnTypeString(ColumnLength(50)),
          ColumnName("address") -> ColumnTypeString(ColumnLength(255)),
          ColumnName("created_at") -> ColumnTypeUnknown,
          ColumnName("updated_at") -> ColumnTypeUnknown
        )
      )
    }
  }

  describe("toSqlSentence") {
    it("success") {
      columnList1
        .merge(columnList2)
        .toSqlSentence shouldBe "address VARCHAR(255), created_at TEXT, id INT(11), name VARCHAR(50), updated_at TEXT"
    }
  }
}
