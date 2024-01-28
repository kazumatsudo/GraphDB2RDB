package domain.table.ddl

import domain.table.ddl.attribute.{
  ForeignKey,
  PrimaryKey,
  UniqueIndex,
  UniqueIndexName
}
import domain.table.ddl.column.ColumnName
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TableAttributesSpec extends AnyFunSpec with Matchers {
  describe("merge") {
    it("success") {
      val primaryKey = PrimaryKey(Set(ColumnName("test1")))
      val foreignKey1 = ForeignKey(
        Map(
          ColumnName("column1") -> (TableName("table1"), ColumnName("column1"))
        )
      )
      val foreignKey2 = ForeignKey(
        Map(
          ColumnName("column2") -> (TableName("table2"), ColumnName("column1"))
        )
      )
      val uniqueIndex1 = UniqueIndex(
        Map(UniqueIndexName("uniqueIndex1") -> Set(ColumnName("test2")))
      )
      val uniqueIndex2 = UniqueIndex(
        Map(UniqueIndexName("uniqueIndex2") -> Set(ColumnName("test3")))
      )

      val tableAttributes =
        TableAttributes(primaryKey, foreignKey1, uniqueIndex1)
      val target = TableAttributes(primaryKey, foreignKey2, uniqueIndex2)

      tableAttributes.merge(
        target,
        checkUnique = false
      ) shouldBe TableAttributes(
        PrimaryKey(Set(ColumnName("test1"))),
        ForeignKey(
          Map(
            ColumnName("column1") -> (TableName("table1"), ColumnName(
              "column1"
            )),
            ColumnName("column2") -> (TableName("table2"), ColumnName(
              "column1"
            ))
          )
        ),
        UniqueIndex(
          Map(
            UniqueIndexName("uniqueIndex1") -> Set(ColumnName("test2")),
            UniqueIndexName("uniqueIndex2") -> Set(ColumnName("test3"))
          )
        )
      )
    }
  }

  describe("toSqlSentenceView") {
    it("success") {
      val primaryKey = PrimaryKey(Set(ColumnName("test1")))
      val foreignKey = ForeignKey(
        Map(
          ColumnName("column1") -> (TableName("table1"), ColumnName("column1"))
        )
      )
      val uniqueIndex = UniqueIndex(
        Map(UniqueIndexName("uniqueIndex") -> Set(ColumnName("test2")))
      )
      val tableAttributes = TableAttributes(primaryKey, foreignKey, uniqueIndex)

      tableAttributes.toSqlSentenceView.toSeq shouldBe List(
        "PRIMARY KEY (test1)",
        "FOREIGN KEY (column1) REFERENCES table1(column1)",
        "UNIQUE INDEX (test2)"
      )
    }
  }
}
