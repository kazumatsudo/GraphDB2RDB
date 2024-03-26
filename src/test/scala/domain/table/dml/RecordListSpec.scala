package domain.table.dml

import domain.table.ddl.TableName
import infrastructure.VertexQuery
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import utils.Config

import java.util
import scala.concurrent.Future

class RecordListSpec extends AsyncFunSpec with Matchers {
  private val config = Config.default

  describe("merge") {
    describe("success") {
      it("if RecordList has no value") {
        val tableName = TableName("test_table")
        val recordId = RecordId(1)
        val recordKey = RecordKey(tableName, recordId)
        val recordValue = RecordValue(Map(("boolean", false)))
        val recordList = RecordList(Map((recordKey, recordValue)))

        RecordList(Map.empty)
          .merge(recordList, checkUnique = true) shouldBe recordList
      }

      it("if the target has no value") {
        val tableName = TableName("test_table")
        val recordId = RecordId(1)
        val recordKey = RecordKey(tableName, recordId)
        val recordValue = RecordValue(Map(("boolean", false)))
        val recordList = RecordList(Map((recordKey, recordValue)))

        recordList.merge(
          RecordList(Map.empty),
          checkUnique = true
        ) shouldBe recordList
      }

      it("if RecordList has the same value") {
        val tableName = TableName("test_table")
        val recordId = RecordId(1)
        val recordKey = RecordKey(tableName, recordId)
        val recordValue = RecordValue(Map(("boolean", false)))
        val recordList = RecordList(Map((recordKey, recordValue)))

        recordList.merge(recordList, checkUnique = true) shouldBe recordList
      }

      it("if checkUnique is false, update after the value") {
        val tableName = TableName("test_table")
        val recordId = RecordId(1)
        val recordKey = RecordKey(tableName, recordId)

        val recordValue1 = RecordValue(Map(("boolean", false)))
        val recordList1 = RecordList(Map((recordKey, recordValue1)))
        val recordValue2 = RecordValue(Map(("int", 1)))
        val recordList2 = RecordList(Map((recordKey, recordValue2)))

        recordList1.merge(recordList2, checkUnique = false) shouldBe recordList2
      }
    }

    describe("failure") {
      it("if RecordList has the same key but the values are not the same") {
        val tableName = TableName("test_table")
        val recordId = RecordId(1)
        val recordKey = RecordKey(tableName, recordId)

        val recordValue1 = RecordValue(Map(("boolean", false)))
        val recordList1 = RecordList(Map((recordKey, recordValue1)))
        val recordValue2 = RecordValue(Map(("int", 1)))
        val recordList2 = RecordList(Map((recordKey, recordValue2)))

        recoverToSucceededIf[IllegalArgumentException] {
          Future { recordList1.merge(recordList2, checkUnique = true) }
        }
      }
    }
  }

  describe("toSqlSentence") {
    it("success") {
      // TODO: not use Vertex
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph, config)
      for {
        count <- vertexQuery.countAll
        vertex <- vertexQuery.getList(0, count.toInt)
        result = vertex
          .map(_.toDml)
          .reduceOption[RecordList] { case (accumulator, currentValue) =>
            accumulator.merge(currentValue, checkUnique = false)
          }
      } yield result shouldBe Some(
        RecordList(
          Map(
            RecordKey((TableName("vertex_person"), RecordId(1))) -> RecordValue(
              Map(
                "id" -> 1,
                "property_age" -> {
                  val propertyAgeValue = new util.ArrayList[Int]
                  propertyAgeValue.add(29)
                  propertyAgeValue
                },
                "property_name" -> {
                  val propertyNameValue = new util.ArrayList[String]
                  propertyNameValue.add("marko")
                  propertyNameValue
                }
              )
            ),
            RecordKey((TableName("vertex_person"), RecordId(2))) -> RecordValue(
              Map(
                "id" -> 2,
                "property_age" -> {
                  val propertyAgeValue = new util.ArrayList[Int]
                  propertyAgeValue.add(27)
                  propertyAgeValue
                },
                "property_name" -> {
                  val propertyNameValue = new util.ArrayList[String]
                  propertyNameValue.add("vadas")
                  propertyNameValue
                }
              )
            ),
            RecordKey(
              (TableName("vertex_software"), RecordId(3))
            ) -> RecordValue(
              Map(
                "id" -> 3,
                "property_lang" -> {
                  val propertyLangValue = new util.ArrayList[String]
                  propertyLangValue.add("java")
                  propertyLangValue
                },
                "property_name" -> {
                  val propertyNameValue = new util.ArrayList[String]
                  propertyNameValue.add("lop")
                  propertyNameValue
                }
              )
            ),
            RecordKey((TableName("vertex_person"), RecordId(4))) -> RecordValue(
              Map(
                "id" -> 4,
                "property_age" -> {
                  val propertyAgeValue = new util.ArrayList[Int]
                  propertyAgeValue.add(32)
                  propertyAgeValue
                },
                "property_name" -> {
                  val propertyNameValue = new util.ArrayList[String]
                  propertyNameValue.add("josh")
                  propertyNameValue
                }
              )
            ),
            RecordKey(
              (TableName("vertex_software"), RecordId(5))
            ) -> RecordValue(
              Map(
                "id" -> 5,
                "property_lang" -> {
                  val propertyLangValue = new util.ArrayList[String]
                  propertyLangValue.add("java")
                  propertyLangValue
                },
                "property_name" -> {
                  val propertyNameValue = new util.ArrayList[String]
                  propertyNameValue.add("ripple")
                  propertyNameValue
                }
              )
            ),
            RecordKey((TableName("vertex_person"), RecordId(6))) -> RecordValue(
              Map(
                "id" -> 6,
                "property_age" -> {
                  val propertyAgeValue = new util.ArrayList[Int]
                  propertyAgeValue.add(35)
                  propertyAgeValue
                },
                "property_name" -> {
                  val propertyNameValue = new util.ArrayList[String]
                  propertyNameValue.add("peter")
                  propertyNameValue
                }
              )
            )
          )
        )
      )
    }
  }
}
