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
        )
      ), Some(
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
            ),
            RecordKey((TableName("edge"), RecordId(10))) -> RecordValue(
              Map(
                "in_v_id" -> 5,
                "out_v_id" -> 4,
                "property_weight" -> 1.0,
                "label_created" -> true
              )
            ),
            RecordKey((TableName("edge"), RecordId(11))) -> RecordValue(
              Map(
                "in_v_id" -> 3,
                "out_v_id" -> 4,
                "property_weight" -> 0.4,
                "label_created" -> true
              )
            ),
            RecordKey((TableName("edge"), RecordId(12))) -> RecordValue(
              Map(
                "in_v_id" -> 3,
                "out_v_id" -> 6,
                "property_weight" -> 0.2,
                "label_created" -> true
              )
            )
          )
        )
      ))
    }
  }
}
