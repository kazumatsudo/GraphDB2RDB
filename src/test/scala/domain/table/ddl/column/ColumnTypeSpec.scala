package domain.table.ddl.column

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ColumnTypeSpec extends AnyFunSpec with Matchers {
  describe("apply") {
    it("success") {
      ColumnType.apply(false) shouldBe ColumnTypeBoolean
      ColumnType.apply(1.toByte) shouldBe ColumnTypeByte(ColumnLength(1))
      ColumnType.apply(1.toShort) shouldBe ColumnTypeShort(ColumnLength(1))
      ColumnType.apply(1) shouldBe ColumnTypeInt(ColumnLength(1))
      ColumnType.apply(1.toLong) shouldBe ColumnTypeLong(ColumnLength(1))
      ColumnType.apply(1.1.toFloat) shouldBe ColumnTypeFloat(ColumnLength(3))
      ColumnType.apply(1.1) shouldBe ColumnTypeDouble(ColumnLength(3))
      ColumnType.apply('a') shouldBe ColumnTypeCharacter(ColumnLength(1))
      ColumnType.apply("string") shouldBe ColumnTypeString(ColumnLength(6))
      ColumnType.apply(Seq.empty) shouldBe ColumnTypeUnknown
    }
  }

  describe("merge") {
    it("success") {
      val columnTypeBoolean = ColumnTypeBoolean
      val columnTypeByteSmall = ColumnTypeByte(ColumnLength(1))
      val columnTypeByteBig = ColumnTypeByte(ColumnLength(2))
      val columnTypeShortSmall = ColumnTypeShort(ColumnLength(1))
      val columnTypeShortBig = ColumnTypeShort(ColumnLength(2))
      val columnTypeIntSmall = ColumnTypeInt(ColumnLength(1))
      val columnTypeIntBig = ColumnTypeInt(ColumnLength(2))
      val columnTypeLongSmall = ColumnTypeLong(ColumnLength(1))
      val columnTypeLongBig = ColumnTypeLong(ColumnLength(2))
      val columnTypeFloatSmall = ColumnTypeFloat(ColumnLength(1))
      val columnTypeFloatBig = ColumnTypeFloat(ColumnLength(2))
      val columnTypeDoubleSmall = ColumnTypeDouble(ColumnLength(1))
      val columnTypeDoubleBig = ColumnTypeDouble(ColumnLength(2))
      val columnTypeCharacterSmall = ColumnTypeCharacter(ColumnLength(1))
      val columnTypeCharacterBig = ColumnTypeCharacter(ColumnLength(2))
      val columnTypeStringSmall = ColumnTypeString(ColumnLength(1))
      val columnTypeStringBig = ColumnTypeString(ColumnLength(2))
      val columnTypeUnknown = ColumnTypeUnknown

      // ColumnTypeBoolean
      ColumnType.merge(
        columnTypeBoolean,
        columnTypeBoolean
      ) shouldBe columnTypeBoolean
      ColumnType.merge(
        columnTypeBoolean,
        columnTypeByteBig
      ) shouldBe ColumnTypeByte(ColumnLength(5))
      ColumnType.merge(
        columnTypeBoolean,
        columnTypeShortBig
      ) shouldBe ColumnTypeShort(ColumnLength(5))
      ColumnType.merge(
        columnTypeBoolean,
        columnTypeIntBig
      ) shouldBe ColumnTypeInt(ColumnLength(5))
      ColumnType.merge(
        columnTypeBoolean,
        columnTypeLongBig
      ) shouldBe ColumnTypeLong(ColumnLength(5))
      ColumnType.merge(
        columnTypeBoolean,
        columnTypeFloatBig
      ) shouldBe ColumnTypeFloat(ColumnLength(5))
      ColumnType.merge(
        columnTypeBoolean,
        columnTypeDoubleBig
      ) shouldBe ColumnTypeDouble(ColumnLength(5))
      ColumnType.merge(
        columnTypeBoolean,
        columnTypeCharacterBig
      ) shouldBe ColumnTypeCharacter(ColumnLength(5))
      ColumnType.merge(
        columnTypeBoolean,
        columnTypeStringBig
      ) shouldBe ColumnTypeString(ColumnLength(5))
      ColumnType.merge(
        columnTypeBoolean,
        columnTypeUnknown
      ) shouldBe columnTypeUnknown

      // ColumnTypeByte
      ColumnType.merge(
        columnTypeByteSmall,
        columnTypeBoolean
      ) shouldBe ColumnTypeByte(ColumnLength(5))
      ColumnType.merge(
        columnTypeByteSmall,
        columnTypeByteBig
      ) shouldBe columnTypeByteBig
      ColumnType.merge(
        columnTypeByteSmall,
        columnTypeShortBig
      ) shouldBe columnTypeShortBig
      ColumnType.merge(
        columnTypeByteSmall,
        columnTypeIntBig
      ) shouldBe columnTypeIntBig
      ColumnType.merge(
        columnTypeByteSmall,
        columnTypeLongBig
      ) shouldBe columnTypeLongBig
      ColumnType.merge(
        columnTypeByteSmall,
        columnTypeFloatBig
      ) shouldBe columnTypeFloatBig
      ColumnType.merge(
        columnTypeByteSmall,
        columnTypeDoubleBig
      ) shouldBe columnTypeDoubleBig
      ColumnType.merge(
        columnTypeByteSmall,
        columnTypeCharacterBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeByteSmall,
        columnTypeStringBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeByteSmall,
        columnTypeUnknown
      ) shouldBe columnTypeUnknown

      // ColumnTypeShort
      ColumnType.merge(
        columnTypeShortSmall,
        columnTypeBoolean
      ) shouldBe ColumnTypeShort(ColumnLength(5))
      ColumnType.merge(
        columnTypeShortSmall,
        columnTypeByteBig
      ) shouldBe columnTypeShortBig
      ColumnType.merge(
        columnTypeShortSmall,
        columnTypeShortBig
      ) shouldBe columnTypeShortBig
      ColumnType.merge(
        columnTypeShortSmall,
        columnTypeIntBig
      ) shouldBe columnTypeIntBig
      ColumnType.merge(
        columnTypeShortSmall,
        columnTypeLongBig
      ) shouldBe columnTypeLongBig
      ColumnType.merge(
        columnTypeShortSmall,
        columnTypeFloatBig
      ) shouldBe columnTypeFloatBig
      ColumnType.merge(
        columnTypeShortSmall,
        columnTypeDoubleBig
      ) shouldBe columnTypeDoubleBig
      ColumnType.merge(
        columnTypeShortSmall,
        columnTypeCharacterBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeShortSmall,
        columnTypeStringBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeShortSmall,
        columnTypeUnknown
      ) shouldBe columnTypeUnknown

      // ColumnTypeInt
      ColumnType.merge(
        columnTypeIntSmall,
        columnTypeBoolean
      ) shouldBe ColumnTypeInt(ColumnLength(5))
      ColumnType.merge(
        columnTypeIntSmall,
        columnTypeByteBig
      ) shouldBe columnTypeIntBig
      ColumnType.merge(
        columnTypeIntSmall,
        columnTypeShortBig
      ) shouldBe columnTypeIntBig
      ColumnType.merge(
        columnTypeIntSmall,
        columnTypeIntBig
      ) shouldBe columnTypeIntBig
      ColumnType.merge(
        columnTypeIntSmall,
        columnTypeLongBig
      ) shouldBe columnTypeLongBig
      ColumnType.merge(
        columnTypeIntSmall,
        columnTypeFloatBig
      ) shouldBe columnTypeFloatBig
      ColumnType.merge(
        columnTypeIntSmall,
        columnTypeDoubleBig
      ) shouldBe columnTypeDoubleBig
      ColumnType.merge(
        columnTypeIntSmall,
        columnTypeCharacterBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeIntSmall,
        columnTypeStringBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeIntSmall,
        columnTypeUnknown
      ) shouldBe columnTypeUnknown

      // ColumnTypeLong
      ColumnType.merge(
        columnTypeLongSmall,
        columnTypeBoolean
      ) shouldBe ColumnTypeLong(ColumnLength(5))
      ColumnType.merge(
        columnTypeLongSmall,
        columnTypeByteBig
      ) shouldBe columnTypeLongBig
      ColumnType.merge(
        columnTypeLongSmall,
        columnTypeShortBig
      ) shouldBe columnTypeLongBig
      ColumnType.merge(
        columnTypeLongSmall,
        columnTypeIntBig
      ) shouldBe columnTypeLongBig
      ColumnType.merge(
        columnTypeLongSmall,
        columnTypeLongBig
      ) shouldBe columnTypeLongBig
      ColumnType.merge(
        columnTypeLongSmall,
        columnTypeFloatBig
      ) shouldBe columnTypeFloatBig
      ColumnType.merge(
        columnTypeLongSmall,
        columnTypeDoubleBig
      ) shouldBe columnTypeDoubleBig
      ColumnType.merge(
        columnTypeLongSmall,
        columnTypeCharacterBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeLongSmall,
        columnTypeStringBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeLongSmall,
        columnTypeUnknown
      ) shouldBe columnTypeUnknown

      // ColumnTypeFloat
      ColumnType.merge(
        columnTypeFloatSmall,
        columnTypeBoolean
      ) shouldBe ColumnTypeFloat(ColumnLength(5))
      ColumnType.merge(
        columnTypeFloatSmall,
        columnTypeByteBig
      ) shouldBe columnTypeFloatBig
      ColumnType.merge(
        columnTypeFloatSmall,
        columnTypeShortBig
      ) shouldBe columnTypeFloatBig
      ColumnType.merge(
        columnTypeFloatSmall,
        columnTypeIntBig
      ) shouldBe columnTypeFloatBig
      ColumnType.merge(
        columnTypeFloatSmall,
        columnTypeLongBig
      ) shouldBe columnTypeFloatBig
      ColumnType.merge(
        columnTypeFloatSmall,
        columnTypeFloatBig
      ) shouldBe columnTypeFloatBig
      ColumnType.merge(
        columnTypeFloatSmall,
        columnTypeDoubleBig
      ) shouldBe columnTypeDoubleBig
      ColumnType.merge(
        columnTypeFloatSmall,
        columnTypeCharacterBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeFloatSmall,
        columnTypeStringBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeFloatSmall,
        columnTypeUnknown
      ) shouldBe columnTypeUnknown

      // ColumnTypeDouble
      ColumnType.merge(
        columnTypeDoubleSmall,
        columnTypeBoolean
      ) shouldBe ColumnTypeDouble(ColumnLength(5))
      ColumnType.merge(
        columnTypeDoubleSmall,
        columnTypeByteBig
      ) shouldBe columnTypeDoubleBig
      ColumnType.merge(
        columnTypeDoubleSmall,
        columnTypeShortBig
      ) shouldBe columnTypeDoubleBig
      ColumnType.merge(
        columnTypeDoubleSmall,
        columnTypeIntBig
      ) shouldBe columnTypeDoubleBig
      ColumnType.merge(
        columnTypeDoubleSmall,
        columnTypeLongBig
      ) shouldBe columnTypeDoubleBig
      ColumnType.merge(
        columnTypeDoubleSmall,
        columnTypeFloatBig
      ) shouldBe columnTypeDoubleBig
      ColumnType.merge(
        columnTypeDoubleSmall,
        columnTypeDoubleBig
      ) shouldBe columnTypeDoubleBig
      ColumnType.merge(
        columnTypeDoubleSmall,
        columnTypeCharacterBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeDoubleSmall,
        columnTypeStringBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeDoubleSmall,
        columnTypeUnknown
      ) shouldBe columnTypeUnknown

      // ColumnTypeCharacter
      ColumnType.merge(
        columnTypeCharacterSmall,
        columnTypeBoolean
      ) shouldBe ColumnTypeCharacter(ColumnLength(5))
      ColumnType.merge(
        columnTypeCharacterSmall,
        columnTypeByteBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeCharacterSmall,
        columnTypeShortBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeCharacterSmall,
        columnTypeIntBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeCharacterSmall,
        columnTypeLongBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeCharacterSmall,
        columnTypeFloatBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeCharacterSmall,
        columnTypeDoubleBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeCharacterSmall,
        columnTypeCharacterBig
      ) shouldBe columnTypeCharacterBig
      ColumnType.merge(
        columnTypeCharacterSmall,
        columnTypeStringBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeCharacterSmall,
        columnTypeUnknown
      ) shouldBe columnTypeUnknown

      // ColumnTypeString
      ColumnType.merge(
        columnTypeStringSmall,
        columnTypeBoolean
      ) shouldBe ColumnTypeString(ColumnLength(5))
      ColumnType.merge(
        columnTypeStringSmall,
        columnTypeByteBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeStringSmall,
        columnTypeShortBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeStringSmall,
        columnTypeIntBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeStringSmall,
        columnTypeLongBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeStringSmall,
        columnTypeFloatBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeStringSmall,
        columnTypeDoubleBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeStringSmall,
        columnTypeCharacterBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeStringSmall,
        columnTypeStringBig
      ) shouldBe columnTypeStringBig
      ColumnType.merge(
        columnTypeStringSmall,
        columnTypeUnknown
      ) shouldBe columnTypeUnknown

      // ColumnTypeUnknown
      ColumnType.merge(
        columnTypeUnknown,
        columnTypeBoolean
      ) shouldBe columnTypeUnknown
      ColumnType.merge(
        columnTypeUnknown,
        columnTypeByteBig
      ) shouldBe columnTypeUnknown
      ColumnType.merge(
        columnTypeUnknown,
        columnTypeShortBig
      ) shouldBe columnTypeUnknown
      ColumnType.merge(
        columnTypeUnknown,
        columnTypeIntBig
      ) shouldBe columnTypeUnknown
      ColumnType.merge(
        columnTypeUnknown,
        columnTypeLongBig
      ) shouldBe columnTypeUnknown
      ColumnType.merge(
        columnTypeUnknown,
        columnTypeFloatBig
      ) shouldBe columnTypeUnknown
      ColumnType.merge(
        columnTypeUnknown,
        columnTypeDoubleBig
      ) shouldBe columnTypeUnknown
      ColumnType.merge(
        columnTypeUnknown,
        columnTypeCharacterBig
      ) shouldBe columnTypeUnknown
      ColumnType.merge(
        columnTypeUnknown,
        columnTypeStringBig
      ) shouldBe columnTypeUnknown
      ColumnType.merge(
        columnTypeUnknown,
        columnTypeUnknown
      ) shouldBe columnTypeUnknown
    }
  }

  describe("toSqlSentence") {
    it("success") {
      ColumnType.apply(false).toSqlSentence shouldBe "BOOLEAN"
      ColumnType.apply(1.toByte).toSqlSentence shouldBe "TINYINT(1)"
      ColumnType.apply(1.toShort).toSqlSentence shouldBe "SMALLINT(1)"
      ColumnType.apply(1).toSqlSentence shouldBe "INT(1)"
      ColumnType.apply(1.toLong).toSqlSentence shouldBe "INT(1)"
      ColumnType.apply(1.1.toFloat).toSqlSentence shouldBe "FLOAT"
      ColumnType.apply(1.1).toSqlSentence shouldBe "DOUBLE"
      ColumnType.apply('a').toSqlSentence shouldBe "CHAR(1)"
      ColumnType.apply(Seq.empty).toSqlSentence shouldBe "TEXT"
    }
  }
}
