package domain.table.column

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ColumnTypeSpec extends AnyFunSpec with Matchers {
  describe("apply") {
    it("success") {
      ColumnType.apply(false) shouldBe ColumnTypeBoolean
      ColumnType.apply("string") shouldBe ColumnTypeString(ColumnLength(6))
      ColumnType.apply(1) shouldBe ColumnTypeInt(ColumnLength(1))
      ColumnType.apply(1.1) shouldBe ColumnTypeDouble(ColumnLength(3))
      ColumnType.apply(Seq.empty) shouldBe ColumnTypeUnknown
    }
  }

  describe("merge") {
    it("success") {
      val columnTypeBoolean = ColumnTypeBoolean
      val columnTypeIntSmall = ColumnTypeInt(ColumnLength(1))
      val columnTypeIntBig = ColumnTypeInt(ColumnLength(2))
      val columnTypeDoubleSmall = ColumnTypeDouble(ColumnLength(1))
      val columnTypeDoubleBig = ColumnTypeDouble(ColumnLength(2))
      val columnTypeStringSmall = ColumnTypeString(ColumnLength(1))
      val columnTypeStringBig = ColumnTypeString(ColumnLength(2))
      val columnTypeUnknown = ColumnTypeUnknown

      // ColumnTypeBoolean
      ColumnType.merge(columnTypeBoolean, columnTypeBoolean) shouldBe columnTypeBoolean
      ColumnType.merge(columnTypeBoolean, columnTypeIntBig) shouldBe ColumnTypeInt(ColumnLength(5))
      ColumnType.merge(columnTypeBoolean, columnTypeDoubleBig) shouldBe ColumnTypeDouble(ColumnLength(5))
      ColumnType.merge(columnTypeBoolean, columnTypeStringBig) shouldBe ColumnTypeString(ColumnLength(5))
      ColumnType.merge(columnTypeBoolean, columnTypeUnknown) shouldBe columnTypeUnknown

      // ColumnTypeInt
      ColumnType.merge(columnTypeIntSmall, columnTypeBoolean) shouldBe ColumnTypeInt(ColumnLength(5))
      ColumnType.merge(columnTypeIntSmall, columnTypeIntBig) shouldBe columnTypeIntBig
      ColumnType.merge(columnTypeIntSmall, columnTypeDoubleBig) shouldBe columnTypeDoubleBig
      ColumnType.merge(columnTypeIntSmall, columnTypeStringBig) shouldBe columnTypeStringBig
      ColumnType.merge(columnTypeIntSmall, columnTypeUnknown) shouldBe columnTypeUnknown

      // ColumnTypeDouble
      ColumnType.merge(columnTypeDoubleSmall, columnTypeBoolean) shouldBe ColumnTypeDouble(ColumnLength(5))
      ColumnType.merge(columnTypeDoubleSmall, columnTypeIntBig) shouldBe ColumnTypeDouble(ColumnLength(2))
      ColumnType.merge(columnTypeDoubleSmall, columnTypeDoubleBig) shouldBe columnTypeDoubleBig
      ColumnType.merge(columnTypeDoubleSmall, columnTypeStringBig) shouldBe columnTypeStringBig
      ColumnType.merge(columnTypeDoubleSmall, columnTypeUnknown) shouldBe columnTypeUnknown

      // ColumnTypeString
      ColumnType.merge(columnTypeStringSmall, columnTypeBoolean) shouldBe ColumnTypeString(ColumnLength(5))
      ColumnType.merge(columnTypeStringSmall, columnTypeIntBig) shouldBe ColumnTypeString(ColumnLength(2))
      ColumnType.merge(columnTypeStringSmall, columnTypeDoubleBig) shouldBe ColumnTypeString(ColumnLength(2))
      ColumnType.merge(columnTypeStringSmall, columnTypeStringBig) shouldBe columnTypeStringBig
      ColumnType.merge(columnTypeStringSmall, columnTypeUnknown) shouldBe columnTypeUnknown

      // ColumnTypeUnknown
      ColumnType.merge(columnTypeUnknown, columnTypeBoolean) shouldBe columnTypeUnknown
      ColumnType.merge(columnTypeUnknown, columnTypeIntBig) shouldBe columnTypeUnknown
      ColumnType.merge(columnTypeUnknown, columnTypeDoubleBig) shouldBe columnTypeUnknown
      ColumnType.merge(columnTypeUnknown, columnTypeStringBig) shouldBe columnTypeUnknown
      ColumnType.merge(columnTypeUnknown, columnTypeUnknown) shouldBe columnTypeUnknown
    }
  }
}
