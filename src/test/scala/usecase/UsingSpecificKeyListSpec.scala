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

      usecase.execute(checkUnique = true) shouldBe (Some(
        TableList(
          Map(
            TableName("vertex") -> ColumnList(
              Map(
                ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("label_person") -> ColumnTypeBoolean,
                ColumnName("property_age") -> ColumnTypeInt(ColumnLength(2)),
                ColumnName("property_name") -> ColumnTypeString(
                  ColumnLength(5)
                )
              )
            )
          )
        )
      ), Some(
        RecordList(
          Map(
            RecordKey((TableName("vertex"), RecordId(1))) -> RecordValue(
              Map(
                "id" -> 1,
                "label_person" -> true,
                "property_age" -> 29,
                "property_name" -> "marko"
              )
            )
          )
        )
      ), Some(
        TableList(
          Map(
            TableName("edge") -> ColumnList(
              Map(
                ColumnName("id_out_v") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("property_weight") -> ColumnTypeDouble(
                  ColumnLength(3)
                ),
                ColumnName("label_knows") -> ColumnTypeBoolean,
                ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("id_in_v") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("label_created") -> ColumnTypeBoolean
              )
            )
          )
        )
      ), Some(
        RecordList(
          Map(
            RecordKey((TableName("edge"), RecordId(7))) -> RecordValue(
              Map(
                "id" -> 7,
                "id_in_v" -> 2,
                "id_out_v" -> 1,
                "label_knows" -> true,
                "property_weight" -> 0.5
              )
            ),
            RecordKey((TableName("edge"), RecordId(8))) -> RecordValue(
              Map(
                "id" -> 8,
                "id_in_v" -> 4,
                "id_out_v" -> 1,
                "label_knows" -> true,
                "property_weight" -> 1.0
              )
            ),
            RecordKey((TableName("edge"), RecordId(9))) -> RecordValue(
              Map(
                "id" -> 9,
                "id_in_v" -> 3,
                "id_out_v" -> 1,
                "label_created" -> true,
                "property_weight" -> 0.4
              )
            )
          )
        )
      ))
    }
  }
}
