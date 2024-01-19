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
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import utils.Config

import scala.collection.parallel.immutable.ParMap

class ByExhaustiveSearchSpec extends AsyncFunSpec with Matchers {
  describe("execute") {
    it("success") {
      val graph = TinkerFactory.createModern().traversal()
      val usecase = ByExhaustiveSearch(graph, Config.default)

      usecase.execute(checkUnique = true).map {
        _ shouldBe UsecaseResponse(
          TableList(
            ParMap(
              TableName("vertex_person") -> ColumnList(
                ParMap(
                  ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("property_age") -> ColumnTypeInt(ColumnLength(2)),
                  ColumnName("property_name") -> ColumnTypeString(
                    ColumnLength(5)
                  )
                )
              ),
              TableName("vertex_software") -> ColumnList(
                ParMap(
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
          ),
          RecordList(
            ParMap(
              RecordKey(
                (TableName("vertex_person"), RecordId(1))
              ) -> RecordValue(
                ParMap(
                  "id" -> 1,
                  "property_age" -> 29,
                  "property_name" -> "marko"
                )
              ),
              RecordKey(
                (TableName("vertex_person"), RecordId(2))
              ) -> RecordValue(
                ParMap(
                  "id" -> 2,
                  "property_age" -> 27,
                  "property_name" -> "vadas"
                )
              ),
              RecordKey(
                (TableName("vertex_person"), RecordId(4))
              ) -> RecordValue(
                ParMap(
                  "id" -> 4,
                  "property_age" -> 32,
                  "property_name" -> "josh"
                )
              ),
              RecordKey(
                (TableName("vertex_person"), RecordId(6))
              ) -> RecordValue(
                ParMap(
                  "id" -> 6,
                  "property_age" -> 35,
                  "property_name" -> "peter"
                )
              ),
              RecordKey(
                (TableName("vertex_software"), RecordId(3))
              ) -> RecordValue(
                ParMap(
                  "id" -> 3,
                  "property_lang" -> "java",
                  "property_name" -> "lop"
                )
              ),
              RecordKey(
                (TableName("vertex_software"), RecordId(5))
              ) -> RecordValue(
                ParMap(
                  "id" -> 5,
                  "property_lang" -> "java",
                  "property_name" -> "ripple"
                )
              )
            )
          ),
          TableList(
            ParMap(
              TableName("edge_created") -> ColumnList(
                ParMap(
                  ColumnName("id") -> ColumnTypeInt(ColumnLength(2)),
                  ColumnName("id_in_v") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("id_out_v") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("property_weight") -> ColumnTypeDouble(
                    ColumnLength(3)
                  )
                )
              ),
              TableName("edge_knows") -> ColumnList(
                ParMap(
                  ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("id_in_v") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("id_out_v") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("property_weight") -> ColumnTypeDouble(
                    ColumnLength(3)
                  )
                )
              )
            )
          ),
          RecordList(
            ParMap(
              RecordKey((TableName("edge_knows"), RecordId(7))) -> RecordValue(
                ParMap(
                  "id" -> 7,
                  "id_in_v" -> 2,
                  "id_out_v" -> 1,
                  "property_weight" -> 0.5
                )
              ),
              RecordKey((TableName("edge_knows"), RecordId(8))) -> RecordValue(
                ParMap(
                  "id" -> 8,
                  "id_in_v" -> 4,
                  "id_out_v" -> 1,
                  "property_weight" -> 1.0
                )
              ),
              RecordKey(
                (TableName("edge_created"), RecordId(9))
              ) -> RecordValue(
                ParMap(
                  "id" -> 9,
                  "id_in_v" -> 3,
                  "id_out_v" -> 1,
                  "property_weight" -> 0.4
                )
              ),
              RecordKey(
                (TableName("edge_created"), RecordId(10))
              ) -> RecordValue(
                ParMap(
                  "id" -> 10,
                  "id_in_v" -> 5,
                  "id_out_v" -> 4,
                  "property_weight" -> 1.0
                )
              ),
              RecordKey(
                (TableName("edge_created"), RecordId(11))
              ) -> RecordValue(
                ParMap(
                  "id" -> 11,
                  "id_in_v" -> 3,
                  "id_out_v" -> 4,
                  "property_weight" -> 0.4
                )
              ),
              RecordKey(
                (TableName("edge_created"), RecordId(12))
              ) -> RecordValue(
                ParMap(
                  "id" -> 12,
                  "id_in_v" -> 3,
                  "id_out_v" -> 6,
                  "property_weight" -> 0.2
                )
              )
            )
          )
        )
      }
    }
  }
}
