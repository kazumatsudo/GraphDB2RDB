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
      columnTypeBoolean.merge(columnTypeBoolean) shouldBe columnTypeBoolean
      columnTypeBoolean.merge(columnTypeIntBig) shouldBe ColumnTypeInt(ColumnLength(5))
      columnTypeBoolean.merge(columnTypeDoubleBig) shouldBe ColumnTypeDouble(ColumnLength(5))
      columnTypeBoolean.merge(columnTypeStringBig) shouldBe ColumnTypeString(ColumnLength(5))
      columnTypeBoolean.merge(columnTypeUnknown) shouldBe columnTypeUnknown

      // ColumnTypeInt
      columnTypeIntSmall.merge(columnTypeBoolean) shouldBe ColumnTypeInt(ColumnLength(5))
      columnTypeIntSmall.merge(columnTypeIntBig) shouldBe columnTypeIntBig
      columnTypeIntSmall.merge(columnTypeDoubleBig) shouldBe columnTypeDoubleBig
      columnTypeIntSmall.merge(columnTypeStringBig) shouldBe columnTypeStringBig
      columnTypeIntSmall.merge(columnTypeUnknown) shouldBe columnTypeUnknown

      // ColumnTypeDouble
      columnTypeDoubleSmall.merge(columnTypeBoolean) shouldBe ColumnTypeDouble(ColumnLength(5))
      columnTypeDoubleSmall.merge(columnTypeIntBig) shouldBe ColumnTypeDouble(ColumnLength(2))
      columnTypeDoubleSmall.merge(columnTypeDoubleBig) shouldBe columnTypeDoubleBig
      columnTypeDoubleSmall.merge(columnTypeStringBig) shouldBe columnTypeStringBig
      columnTypeDoubleSmall.merge(columnTypeUnknown) shouldBe columnTypeUnknown

      // ColumnTypeString
      columnTypeStringSmall.merge(columnTypeBoolean) shouldBe ColumnTypeString(ColumnLength(5))
      columnTypeStringSmall.merge(columnTypeIntBig) shouldBe ColumnTypeString(ColumnLength(2))
      columnTypeStringSmall.merge(columnTypeDoubleBig) shouldBe ColumnTypeString(ColumnLength(2))
      columnTypeStringSmall.merge(columnTypeStringBig) shouldBe columnTypeStringBig
      columnTypeStringSmall.merge(columnTypeUnknown) shouldBe columnTypeUnknown

      // ColumnTypeUnknown
      columnTypeUnknown.merge(columnTypeBoolean) shouldBe columnTypeUnknown
      columnTypeUnknown.merge(columnTypeIntBig) shouldBe columnTypeUnknown
      columnTypeUnknown.merge(columnTypeDoubleBig) shouldBe columnTypeUnknown
      columnTypeUnknown.merge(columnTypeStringBig) shouldBe columnTypeUnknown
      columnTypeUnknown.merge(columnTypeUnknown) shouldBe columnTypeUnknown
    }
  }
}
