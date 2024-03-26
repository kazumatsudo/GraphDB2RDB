package domain.table.dml

import org.janusgraph.graphdb.relations.RelationIdentifier
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.sql.Timestamp
import java.time.Instant
import java.util

class RecordValueSpec extends AnyFunSpec with Matchers {
  describe("toSqlSentence") {
    it("success") {
      // Boolean
      RecordValue(
        Map(("boolean", false))
      ).toSqlSentence shouldBe ("boolean", "false")

      // Byte
      RecordValue(Map(("byte", 1.toByte))).toSqlSentence shouldBe ("byte", "1")

      // Short
      RecordValue(
        Map(("short", 1.toShort))
      ).toSqlSentence shouldBe ("short", "1")

      // Int
      RecordValue(Map(("int", 1))).toSqlSentence shouldBe ("int", "1")

      // Long
      RecordValue(Map(("long", 1.toLong))).toSqlSentence shouldBe ("long", "1")

      // Float
      RecordValue(
        Map(("float", 1.toFloat))
      ).toSqlSentence shouldBe ("float", "1.0")

      // Double
      RecordValue(
        Map(("double", 1.toDouble))
      ).toSqlSentence shouldBe ("double", "1.0")

      // util.UUID
      val uuid = util.UUID.randomUUID()
      RecordValue(
        Map(("uuid", uuid))
      ).toSqlSentence shouldBe ("uuid", s"'$uuid'")

      // Date
      val dateMin = Instant.MIN
      RecordValue(
        Map(("dateMin", dateMin))
      ).toSqlSentence shouldBe ("dateMin", s"'${Timestamp.from(dateMin)}'")
      val dateMax = Instant.MAX
      RecordValue(
        Map(("dateMax", dateMax))
      ).toSqlSentence shouldBe ("dateMax", s"'${Timestamp.from(dateMax)}'")

      // Char
      RecordValue(
        Map(("char", 'a'))
      ).toSqlSentence shouldBe ("char", "'a'")

      // String
      RecordValue(
        Map(("string", 1.toString))
      ).toSqlSentence shouldBe ("string", "'1'")

      // util.ArrayList
      val arrayList1 = new util.ArrayList[String]()
      arrayList1.add("a")
      RecordValue(
        Map(("arrayList1", arrayList1))
      ).toSqlSentence shouldBe ("arrayList1", "'a'")

      val arrayList2 = new util.ArrayList[String]()
      arrayList2.add("a")
      arrayList2.add("a")
      RecordValue(
        Map(("arrayList2", arrayList2))
      ).toSqlSentence shouldBe ("arrayList2", "'[a, a]'")

      // RelationIdentifier
      val relationIdentifier =
        new RelationIdentifier(1L, 1, 1, 1L)
      RecordValue(
        Map("RelationIdentifier" -> relationIdentifier)
      ).toSqlSentence shouldBe ("RelationIdentifier", s"'${relationIdentifier.toString}'")

      // the other
      RecordValue(
        Map(("unknown", Seq(1)))
      ).toSqlSentence shouldBe ("unknown", "'List(1)'")
    }
  }
}
