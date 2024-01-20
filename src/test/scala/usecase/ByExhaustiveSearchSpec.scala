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

class ByExhaustiveSearchSpec extends AnyFunSpec with Matchers {
  private val config = Config.default

  describe("execute") {
    it("success") {
      val graph = TinkerFactory.createModern().traversal()
      val usecase = ByExhaustiveSearch(graph, config)

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
            ),
            TableName("vertex_software") -> ColumnList(
              Map(
                ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("property_lang") -> ColumnTypeString(
                  ColumnLength(4)
                ),
                ColumnName("property_name") -> ColumnTypeString(
                  ColumnLength(6)
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
            ),
            RecordKey((TableName("vertex_person"), RecordId(2))) -> RecordValue(
              Map("id" -> 2, "property_age" -> 27, "property_name" -> "vadas")
            ),
            RecordKey((TableName("vertex_person"), RecordId(4))) -> RecordValue(
              Map("id" -> 4, "property_age" -> 32, "property_name" -> "josh")
            ),
            RecordKey((TableName("vertex_person"), RecordId(6))) -> RecordValue(
              Map("id" -> 6, "property_age" -> 35, "property_name" -> "peter")
            ),
            RecordKey(
              (TableName("vertex_software"), RecordId(3))
            ) -> RecordValue(
              Map(
                "id" -> 3,
                "property_lang" -> "java",
                "property_name" -> "lop"
              )
            ),
            RecordKey(
              (TableName("vertex_software"), RecordId(5))
            ) -> RecordValue(
              Map(
                "id" -> 5,
                "property_lang" -> "java",
                "property_name" -> "ripple"
              )
            )
          )
        )
      ), Some(
        TableList(
          Map(
            TableName("edge_created") -> ColumnList(
              Map(
                ColumnName("id") -> ColumnTypeInt(ColumnLength(2)),
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
            ),
            RecordKey((TableName("edge_created"), RecordId(10))) -> RecordValue(
              Map(
                "id" -> 10,
                "id_in_v" -> 5,
                "id_out_v" -> 4,
                "property_weight" -> 1.0
              )
            ),
            RecordKey((TableName("edge_created"), RecordId(11))) -> RecordValue(
              Map(
                "id" -> 11,
                "id_in_v" -> 3,
                "id_out_v" -> 4,
                "property_weight" -> 0.4
              )
            ),
            RecordKey((TableName("edge_created"), RecordId(12))) -> RecordValue(
              Map(
                "id" -> 12,
                "id_in_v" -> 3,
                "id_out_v" -> 6,
                "property_weight" -> 0.2
              )
            )
          )
        )
      ))
    }
  }
}
