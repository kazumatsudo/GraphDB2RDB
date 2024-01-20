package usecase

import domain.table.ddl.column.{
  ColumnLength,
  ColumnList,
  ColumnName,
  ColumnTypeDouble,
  ColumnTypeInt,
  ColumnTypeString
}
import domain.table.ddl.{TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import utils.Config

class UsingSpecificKeyListSpec extends AnyFunSpec with Matchers {
  private val config = Config.default

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
      val usecase = UsingSpecificKeyList(graph, config, value)

      usecase.execute(checkUnique = true) shouldBe (Some(
        TableList(
          Map(
            TableName("vertex_person") -> ColumnList(
              Map(
                ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
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
            RecordKey((TableName("vertex_person"), RecordId(1))) -> RecordValue(
              Map("id" -> 1, "property_age" -> 29, "property_name" -> "marko")
            )
          )
        )
      ), Some(
        TableList(
          Map(
            TableName("edge_created") -> ColumnList(
              Map(
                ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("id_in_v") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("id_out_v") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("property_weight") -> ColumnTypeDouble(
                  ColumnLength(3)
                )
              )
            ),
            TableName("edge_knows") -> ColumnList(
              Map(
                ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("id_in_v") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("id_out_v") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("property_weight") -> ColumnTypeDouble(
                  ColumnLength(3)
                )
              )
            )
          )
        )
      ), Some(
        RecordList(
          Map(
            RecordKey((TableName("edge_knows"), RecordId(7))) -> RecordValue(
              Map(
                "id" -> 7,
                "id_in_v" -> 2,
                "id_out_v" -> 1,
                "property_weight" -> 0.5
              )
            ),
            RecordKey((TableName("edge_knows"), RecordId(8))) -> RecordValue(
              Map(
                "id" -> 8,
                "id_in_v" -> 4,
                "id_out_v" -> 1,
                "property_weight" -> 1.0
              )
            ),
            RecordKey((TableName("edge_created"), RecordId(9))) -> RecordValue(
              Map(
                "id" -> 9,
                "id_in_v" -> 3,
                "id_out_v" -> 1,
                "property_weight" -> 0.4
              )
            )
          )
        )
      ))
    }
  }
}
