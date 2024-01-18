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

class ByExhaustiveSearchSpec extends AnyFunSpec with Matchers {
  describe("execute") {
    it("success") {
      val graph = TinkerFactory.createModern().traversal()
      val usecase = ByExhaustiveSearch(graph)

      usecase.execute(checkUnique = true) shouldBe (Some(
        TableList(
          Map(
            TableName("vertex") -> ColumnList(
              Map(
                ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("property_age") -> ColumnTypeInt(ColumnLength(2)),
                ColumnName("property_lang") -> ColumnTypeString(
                  ColumnLength(4)
                ),
                ColumnName("property_name") -> ColumnTypeString(
                  ColumnLength(6)
                ),
                ColumnName("label_software") -> ColumnTypeBoolean,
                ColumnName("label_person") -> ColumnTypeBoolean
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
                "property_age" -> 29,
                "property_name" -> "marko",
                "label_person" -> true
              )
            ),
            RecordKey((TableName("vertex"), RecordId(2))) -> RecordValue(
              Map(
                "id" -> 2,
                "property_age" -> 27,
                "property_name" -> "vadas",
                "label_person" -> true
              )
            ),
            RecordKey((TableName("vertex"), RecordId(3))) -> RecordValue(
              Map(
                "id" -> 3,
                "property_lang" -> "java",
                "property_name" -> "lop",
                "label_software" -> true
              )
            ),
            RecordKey((TableName("vertex"), RecordId(4))) -> RecordValue(
              Map(
                "id" -> 4,
                "property_age" -> 32,
                "property_name" -> "josh",
                "label_person" -> true
              )
            ),
            RecordKey((TableName("vertex"), RecordId(5))) -> RecordValue(
              Map(
                "id" -> 5,
                "property_lang" -> "java",
                "property_name" -> "ripple",
                "label_software" -> true
              )
            ),
            RecordKey((TableName("vertex"), RecordId(6))) -> RecordValue(
              Map(
                "id" -> 6,
                "property_age" -> 35,
                "property_name" -> "peter",
                "label_person" -> true
              )
            )
          )
        )
      ), Some(
        TableList(
          Map(
            TableName("edge") -> ColumnList(
              Map(
                ColumnName("id") -> ColumnTypeInt(ColumnLength(2)),
                ColumnName("id_in_v") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("id_out_v") -> ColumnTypeInt(ColumnLength(1)),
                ColumnName("label_created") -> ColumnTypeBoolean,
                ColumnName("label_knows") -> ColumnTypeBoolean,
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
            ),
            RecordKey((TableName("edge"), RecordId(10))) -> RecordValue(
              Map(
                "id" -> 10,
                "id_in_v" -> 5,
                "id_out_v" -> 4,
                "label_created" -> true,
                "property_weight" -> 1.0
              )
            ),
            RecordKey((TableName("edge"), RecordId(11))) -> RecordValue(
              Map(
                "id" -> 11,
                "id_in_v" -> 3,
                "id_out_v" -> 4,
                "label_created" -> true,
                "property_weight" -> 0.4
              )
            ),
            RecordKey((TableName("edge"), RecordId(12))) -> RecordValue(
              Map(
                "id" -> 12,
                "id_in_v" -> 3,
                "id_out_v" -> 6,
                "label_created" -> true,
                "property_weight" -> 0.2
              )
            ),
          )
        )
      ))
    }
  }
}
