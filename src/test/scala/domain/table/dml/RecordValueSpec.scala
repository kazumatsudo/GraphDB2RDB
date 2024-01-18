package domain.table.dml

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class RecordValueSpec extends AnyFunSpec with Matchers {
  describe("toSqlSentence") {
    it("success") {
      RecordValue(
        Map(("boolean", false))
      ).toSqlSentence shouldBe ("boolean", "false")
      RecordValue(Map(("byte", 1.toByte))).toSqlSentence shouldBe ("byte", "1")
      RecordValue(
        Map(("short", 1.toShort))
      ).toSqlSentence shouldBe ("short", "1")
      RecordValue(Map(("int", 1))).toSqlSentence shouldBe ("int", "1")
      RecordValue(Map(("long", 1.toLong))).toSqlSentence shouldBe ("long", "1")
      RecordValue(
        Map(("float", 1.toFloat))
      ).toSqlSentence shouldBe ("float", "1.0")
      RecordValue(
        Map(("double", 1.toDouble))
      ).toSqlSentence shouldBe ("double", "1.0")
      val uuid = UUID.randomUUID()
      RecordValue(
        Map(("uuid", uuid))
      ).toSqlSentence shouldBe ("uuid", s"\"$uuid\"")
      RecordValue(
        Map(("char", 'a'))
      ).toSqlSentence shouldBe ("char", "\"a\"")
      RecordValue(
        Map(("string", 1.toString))
      ).toSqlSentence shouldBe ("string", "\"1\"")
      RecordValue(
        Map(("unknown", Seq(1)))
      ).toSqlSentence shouldBe ("unknown", "\"List(1)\"")
    }
  }
}
