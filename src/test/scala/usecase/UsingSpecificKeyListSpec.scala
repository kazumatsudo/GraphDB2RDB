package usecase

import domain.table.ddl.attribute.{
  ForeignKey,
  PrimaryKey,
  UniqueIndex,
  UniqueIndexName
}
import domain.table.ddl.column.{
  ColumnLength,
  ColumnList,
  ColumnName,
  ColumnTypeDouble,
  ColumnTypeInt,
  ColumnTypeString
}
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
            Map(
              RecordKey(
                (TableName("vertex_person"), RecordId(1))
              ) -> RecordValue(
                Map("id" -> 1, "property_name" -> "marko", "property_age" -> 29)
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
            Map(
              RecordKey(
                (TableName("edge_knows_from_person_to_person"), RecordId(8))
              ) -> RecordValue(
                Map(
                  "id" -> 8,
                  "id_in_v" -> 4,
                  "id_out_v" -> 1,
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
                (TableName("edge_knows_from_person_to_person"), RecordId(7))
              ) -> RecordValue(
                Map(
                  "id" -> 7,
                  "id_in_v" -> 2,
                  "id_out_v" -> 1,
                  "property_weight" -> 0.5
                )
              )
            )
          )
        )
      }
    }
  }
}
