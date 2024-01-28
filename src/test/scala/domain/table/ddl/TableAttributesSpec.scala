package domain.table.ddl

import domain.table.ddl.attribute.{PrimaryKey, UniqueIndex, UniqueIndexName}
import domain.table.ddl.column.ColumnName
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TableAttributesSpec extends AnyFunSpec with Matchers {
  describe("merge") {
    it("success") {
      val primaryKey = PrimaryKey(Set(ColumnName("test1")))
      val uniqueIndex1 = UniqueIndex(
        Map(UniqueIndexName("uniqueIndex1") -> Set(ColumnName("test2")))
      )
      val uniqueIndex2 = UniqueIndex(
        Map(UniqueIndexName("uniqueIndex2") -> Set(ColumnName("test3")))
      )

      val tableAttributes = TableAttributes(primaryKey, uniqueIndex1)
      val target = TableAttributes(primaryKey, uniqueIndex2)

      tableAttributes.merge(
        target,
        checkUnique = false
      ) shouldBe TableAttributes(
        PrimaryKey(Set(ColumnName("test1"))),
        UniqueIndex(
          Map(
            UniqueIndexName("uniqueIndex1") -> Set(ColumnName("test2")),
            UniqueIndexName("uniqueIndex2") -> Set(ColumnName("test3"))
          )
        )
      )
    }
  }

  describe("toSqlSentenceSeq") {
    it("success") {
      val primaryKey = PrimaryKey(Set(ColumnName("test1")))
      val uniqueIndex = UniqueIndex(
        Map(UniqueIndexName("uniqueIndex") -> Set(ColumnName("test2")))
      )
      val tableAttributes = TableAttributes(primaryKey, uniqueIndex)

      tableAttributes.toSqlSentenceSeq shouldBe List(
        "PRIMARY KEY (test1)",
        "UNIQUE INDEX (test2)"
      )
    }
  }
}
