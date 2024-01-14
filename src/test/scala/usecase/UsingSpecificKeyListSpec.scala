package usecase

import domain.table.ddl.column.{
  ColumnLength,
  ColumnList,
  ColumnName,
  ColumnTypeBoolean,
  ColumnTypeDouble,
  ColumnTypeInt,
  ColumnTypeString
}
import domain.table.ddl.{TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class UsingSpecificKeyListSpec extends AnyFunSpec with Matchers {
  describe("execute") {
    it("success") {
      val graph = TinkerFactory.createModern().traversal()

      val value = UsingSpecificKeyListRequest(
        Seq(
          UsingSpecificKeyListRequestLabel(
            "person",
            Seq(UsingSpecificKeyListRequestKey("age", Seq(29)))
          )
        )
      )
      val usecase = UsingSpecificKeyList(graph, value)

      usecase.execute shouldBe (Some(
        (
          TableList(
            Map(
              TableName("vertex") -> ColumnList(
                Map(
                  ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("property_age") -> ColumnTypeInt(ColumnLength(2)),
                  ColumnName("property_name") -> ColumnTypeString(
                    ColumnLength(5)
                  ),
                  ColumnName("label_person") -> ColumnTypeBoolean
                )
              )
            )
          ),
          RecordList(
            Map(
              RecordKey((TableName("vertex"), RecordId(1))) -> RecordValue(
                Map(
                  "id" -> 1,
                  "property_age" -> 29,
                  "property_name" -> "marko",
                  "label_person" -> true
                )
              )
            )
          )
        )
      ), Some(
        (
          TableList(
            Map(
              TableName("edge") -> ColumnList(
                Map(
                  ColumnName("in_v_id") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("out_v_id") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("property_weight") -> ColumnTypeDouble(
                    ColumnLength(3)
                  ),
                  ColumnName("label_created") -> ColumnTypeBoolean,
                  ColumnName("label_knows") -> ColumnTypeBoolean
                )
              )
            )
          ),
          RecordList(
            Map(
              RecordKey((TableName("edge"), RecordId(7))) -> RecordValue(
                Map(
                  "in_v_id" -> 2,
                  "out_v_id" -> 1,
                  "property_weight" -> 0.5,
                  "label_knows" -> true
                )
              ),
              RecordKey((TableName("edge"), RecordId(8))) -> RecordValue(
                Map(
                  "in_v_id" -> 4,
                  "out_v_id" -> 1,
                  "property_weight" -> 1.0,
                  "label_knows" -> true
                )
              ),
              RecordKey((TableName("edge"), RecordId(9))) -> RecordValue(
                Map(
                  "in_v_id" -> 3,
                  "out_v_id" -> 1,
                  "property_weight" -> 0.4,
                  "label_created" -> true
                )
              )
            )
          )
        )
      ))
    }
  }
}
