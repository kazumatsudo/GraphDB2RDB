package domain.table.ddl.attribute

import domain.table.ddl.TableName
import domain.table.ddl.column.ColumnName
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ForeignKeySpec extends AnyFunSpec with Matchers {
  describe("merge") {
    it("if ForeignKey value has no value") {
      val columnName = ColumnName("column1")
      val reference = (TableName("table1"), ColumnName("column1"))
      val foreignKey = ForeignKey(Map(columnName -> reference))

      ForeignKey(Map.empty)
        .merge(foreignKey, checkUnique = true) shouldBe foreignKey
    }

    it("if the target has no value") {
      val columnName = ColumnName("column1")
      val reference = (TableName("table1"), ColumnName("column1"))
      val foreignKey = ForeignKey(Map(columnName -> reference))

      foreignKey.merge(
        ForeignKey(Map.empty),
        checkUnique = true
      ) shouldBe foreignKey
    }

    it("if ForeignKey has the same value") {
      val columnName = ColumnName("column1")
      val reference = (TableName("table1"), ColumnName("column1"))
      val foreignKey = ForeignKey(Map(columnName -> reference))

      foreignKey.merge(foreignKey, checkUnique = true) shouldBe foreignKey
    }

    it("if ForeignKey has not the same value") {
      val columnName = ColumnName("column1")

      val reference1 = (TableName("table1"), ColumnName("column1"))
      val foreignKey1 = ForeignKey(Map(columnName -> reference1))
      val reference2 = (TableName("table2"), ColumnName("column2"))
      val foreignKey2 = ForeignKey(Map(columnName -> reference2))

      intercept[IllegalArgumentException] {
        foreignKey1.merge(foreignKey2, checkUnique = true)
      }
    }

    it("if ForeignKey is false, update after the value") {
      val columnName = ColumnName("column1")

      val reference1 = (TableName("table1"), ColumnName("column1"))
      val foreignKey1 = ForeignKey(Map(columnName -> reference1))
      val reference2 = (TableName("table2"), ColumnName("column2"))
      val foreignKey2 = ForeignKey(Map(columnName -> reference2))

      foreignKey1.merge(
        foreignKey2,
        checkUnique = false
      ) shouldBe foreignKey2
    }
  }

  describe("toSqlSentenceView") {
    it("success") {
      val columnName1 = ColumnName("column1")
      val reference1 = (TableName("table1"), ColumnName("column1"))
      val columnName2 = ColumnName("column2")
      val reference2 = (TableName("table2"), ColumnName("column2"))
      val foreignKey =
        ForeignKey(Map(columnName1 -> reference1, columnName2 -> reference2))

      foreignKey.toSqlSentenceView.toSeq shouldBe List(
        "FOREIGN KEY (column1) REFERENCES table1(column1)",
        "FOREIGN KEY (column2) REFERENCES table2(column2)"
      )
    }
  }
}
