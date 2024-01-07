package domain.table.column

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ColumnListSpec extends AnyFunSpec with Matchers {

  describe("merge") {
    it("success") {
      val columnList1 = ColumnList(Map(
        ColumnName("id") -> ColumnTypeInt(ColumnLength(11)),
        ColumnName("name") -> ColumnTypeString(ColumnLength(30)),
        ColumnName("address") -> ColumnTypeString(ColumnLength(255)),
      ))
      val columnList2 = ColumnList(Map(
        ColumnName("id") -> ColumnTypeInt(ColumnLength(11)),
        ColumnName("name") -> ColumnTypeString(ColumnLength(50)),
        ColumnName("created_at") -> ColumnTypeUnknown,
        ColumnName("updated_at") -> ColumnTypeUnknown,
      ))

      columnList1.merge(columnList2) shouldBe ColumnList(Map(
        ColumnName("id") -> ColumnTypeInt(ColumnLength(11)),
        ColumnName("name") -> ColumnTypeString(ColumnLength(50)),
        ColumnName("address") -> ColumnTypeString(ColumnLength(255)),
        ColumnName("created_at") -> ColumnTypeUnknown,
        ColumnName("updated_at") -> ColumnTypeUnknown,
      ))
    }
  }
}
