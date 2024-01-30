package usecase

import domain.table.ddl.attribute.{ForeignKey, PrimaryKey, UniqueIndex, UniqueIndexName}
import domain.table.ddl.column.{ColumnLength, ColumnList, ColumnName, ColumnTypeDouble, ColumnTypeInt, ColumnTypeString}
import domain.table.ddl.{TableAttributes, TableList, TableName}
import domain.table.dml.{RecordId, RecordKey, RecordList, RecordValue}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import utils.Config

import scala.collection.immutable.HashMap

class UsingSpecificKeyListSpec extends AsyncFunSpec with Matchers {
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

      usecase.execute(checkUnique = true).map {
        _ shouldBe UsecaseResponse(
          TableList(
            Map(
              TableName("vertex_software") -> (ColumnList(
                Map(
                  ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("property_name") -> ColumnTypeString(
                    ColumnLength(6)
                  ),
                  ColumnName("property_lang") -> ColumnTypeString(
                    ColumnLength(4)
                  )
                )
              ), TableAttributes(
                PrimaryKey(Set(ColumnName("id"))),
                ForeignKey(Map()),
                UniqueIndex(Map())
              )),
              TableName("vertex_person") -> (ColumnList(
                Map(
                  ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("property_name") -> ColumnTypeString(
                    ColumnLength(5)
                  ),
                  ColumnName("property_age") -> ColumnTypeInt(ColumnLength(2))
                )
              ), TableAttributes(
                PrimaryKey(Set(ColumnName("id"))),
                ForeignKey(Map()),
                UniqueIndex(Map())
              ))
            )
          ),
          RecordList(
            HashMap(
              RecordKey(
                (TableName("vertex_software"), RecordId(5))
              ) -> RecordValue(
                Map(
                  "id" -> 5,
                  "property_name" -> "ripple",
                  "property_lang" -> "java"
                )
              ),
              RecordKey(
                (TableName("vertex_software"), RecordId(3))
              ) -> RecordValue(
                Map(
                  "id" -> 3,
                  "property_name" -> "lop",
                  "property_lang" -> "java"
                )
              ),
              RecordKey(
                (TableName("vertex_person"), RecordId(2))
              ) -> RecordValue(
                Map("id" -> 2, "property_name" -> "vadas", "property_age" -> 27)
              ),
              RecordKey(
                (TableName("vertex_person"), RecordId(6))
              ) -> RecordValue(
                Map("id" -> 6, "property_name" -> "peter", "property_age" -> 35)
              ),
              RecordKey(
                (TableName("vertex_person"), RecordId(4))
              ) -> RecordValue(
                Map("id" -> 4, "property_name" -> "josh", "property_age" -> 32)
              )
            )
          ),
          TableList(
            Map(
              TableName("edge_knows_from_person_to_person") -> (ColumnList(
                Map(
                  ColumnName("id") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("id_in_v") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("id_out_v") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("property_weight") -> ColumnTypeDouble(
                    ColumnLength(3)
                  )
                )
              ), TableAttributes(
                PrimaryKey(Set(ColumnName("id"))),
                ForeignKey(
                  Map(
                    ColumnName("id_in_v") -> (TableName(
                      "vertex_person"
                    ), ColumnName("id")),
                    ColumnName("id_out_v") -> (TableName(
                      "vertex_person"
                    ), ColumnName("id"))
                  )
                ),
                UniqueIndex(
                  Map(
                    UniqueIndexName("index_id_in_v_id_out_v") -> Set(
                      ColumnName("id_in_v"),
                      ColumnName("id_out_v")
                    )
                  )
                )
              )),
              TableName("edge_created_from_person_to_software") -> (ColumnList(
                Map(
                  ColumnName("id") -> ColumnTypeInt(ColumnLength(2)),
                  ColumnName("id_in_v") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("id_out_v") -> ColumnTypeInt(ColumnLength(1)),
                  ColumnName("property_weight") -> ColumnTypeDouble(
                    ColumnLength(3)
                  )
                )
              ), TableAttributes(
                PrimaryKey(Set(ColumnName("id"))),
                ForeignKey(
                  Map(
                    ColumnName("id_in_v") -> (TableName(
                      "vertex_software"
                    ), ColumnName("id")),
                    ColumnName("id_out_v") -> (TableName(
                      "vertex_person"
                    ), ColumnName("id"))
                  )
                ),
                UniqueIndex(
                  Map(
                    UniqueIndexName("index_id_in_v_id_out_v") -> Set(
                      ColumnName("id_in_v"),
                      ColumnName("id_out_v")
                    )
                  )
                )
              ))
            )
          ),
          RecordList(
            HashMap(
              RecordKey(
                (
                  TableName("edge_created_from_person_to_software"),
                  RecordId(11)
                )
              ) -> RecordValue(
                Map(
                  "id" -> 11,
                  "id_in_v" -> 3,
                  "id_out_v" -> 4,
                  "property_weight" -> 0.4
                )
              ),
              RecordKey(
                (TableName("edge_knows_from_person_to_person"), RecordId(7))
              ) -> RecordValue(
                Map(
                  "id" -> 7,
                  "id_in_v" -> 2,
                  "id_out_v" -> 1,
                  "property_weight" -> 0.5
                )
              ),
              RecordKey(
                (
                  TableName("edge_created_from_person_to_software"),
                  RecordId(10)
                )
              ) -> RecordValue(
                Map(
                  "id" -> 10,
                  "id_in_v" -> 5,
                  "id_out_v" -> 4,
                  "property_weight" -> 1.0
                )
              ),
              RecordKey(
                (TableName("edge_created_from_person_to_software"), RecordId(9))
              ) -> RecordValue(
                Map(
                  "id" -> 9,
                  "id_in_v" -> 3,
                  "id_out_v" -> 1,
                  "property_weight" -> 0.4
                )
              ),
              RecordKey(
                (
                  TableName("edge_created_from_person_to_software"),
                  RecordId(12)
                )
              ) -> RecordValue(
                Map(
                  "id" -> 12,
                  "id_in_v" -> 3,
                  "id_out_v" -> 6,
                  "property_weight" -> 0.2
                )
              ),
              RecordKey(
                (TableName("edge_knows_from_person_to_person"), RecordId(8))
              ) -> RecordValue(
                Map(
                  "id" -> 8,
                  "id_in_v" -> 4,
                  "id_out_v" -> 1,
                  "property_weight" -> 1.0
                )
              )
            )
          )
        )
      }
    }
  }
}
