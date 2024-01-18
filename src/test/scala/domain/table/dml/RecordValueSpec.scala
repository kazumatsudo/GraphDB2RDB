package domain.table.dml

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID
import scala.collection.parallel.immutable.ParMap

class RecordValueSpec extends AnyFunSpec with Matchers {
  describe("toSqlSentence") {
    it("success") {
      RecordValue(
        ParMap(("boolean", false))
      ).toSqlSentence shouldBe ("boolean", "false")
      RecordValue(
        ParMap(("byte", 1.toByte))
      ).toSqlSentence shouldBe ("byte", "1")
      RecordValue(
        ParMap(("short", 1.toShort))
      ).toSqlSentence shouldBe ("short", "1")
      RecordValue(ParMap(("int", 1))).toSqlSentence shouldBe ("int", "1")
      RecordValue(
        ParMap(("long", 1.toLong))
      ).toSqlSentence shouldBe ("long", "1")
      RecordValue(
        ParMap(("float", 1.toFloat))
      ).toSqlSentence shouldBe ("float", "1.0")
      RecordValue(
        ParMap(("double", 1.toDouble))
      ).toSqlSentence shouldBe ("double", "1.0")
      val uuid = UUID.randomUUID()
      RecordValue(
        ParMap(("uuid", uuid))
      ).toSqlSentence shouldBe ("uuid", s"\"$uuid\"")
      RecordValue(
        ParMap(("char", 'a'))
      ).toSqlSentence shouldBe ("char", "\"a\"")
      RecordValue(
        ParMap(("string", 1.toString))
      ).toSqlSentence shouldBe ("string", "\"1\"")
      RecordValue(
        ParMap(("unknown", Seq(1)))
      ).toSqlSentence shouldBe ("unknown", "\"List(1)\"")
    }
  }
}
