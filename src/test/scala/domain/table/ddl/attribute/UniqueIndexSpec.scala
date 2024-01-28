package domain.table.ddl.attribute

import domain.table.ddl.column.ColumnName
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class UniqueIndexSpec extends AnyFunSpec with Matchers {
  describe("merge") {
    it("if UniqueIndex value has no value") {
      val uniqueIndexName = UniqueIndexName("index")
      val columnNameSet = Set(ColumnName("column1"))
      val uniqueIndex = UniqueIndex(Map(uniqueIndexName -> columnNameSet))

      UniqueIndex(Map.empty)
        .merge(uniqueIndex, checkUnique = true) shouldBe uniqueIndex
    }

    it("if the target has no value") {
      val uniqueIndexName = UniqueIndexName("index")
      val columnNameSet = Set(ColumnName("column1"))
      val uniqueIndex = UniqueIndex(Map(uniqueIndexName -> columnNameSet))

      uniqueIndex.merge(
        UniqueIndex(Map.empty),
        checkUnique = true
      ) shouldBe uniqueIndex
    }

    it("if UniqueIndex has the same value") {
      val uniqueIndexName = UniqueIndexName("index")
      val columnNameSet = Set(ColumnName("column1"))
      val uniqueIndex = UniqueIndex(Map(uniqueIndexName -> columnNameSet))

      uniqueIndex.merge(uniqueIndex, checkUnique = true) shouldBe uniqueIndex
    }

    it("if UniqueIndex has not the same value") {
      val uniqueIndexName = UniqueIndexName("index")

      val columnNameSet1 = Set(ColumnName("column1"))
      val uniqueIndex1 = UniqueIndex(Map(uniqueIndexName -> columnNameSet1))
      val columnNameSet2 = Set(ColumnName("column2"))
      val uniqueIndex2 = UniqueIndex(Map(uniqueIndexName -> columnNameSet2))

      intercept[IllegalArgumentException] {
        uniqueIndex1.merge(uniqueIndex2, checkUnique = true)
      }
    }

    it("if checkUnique is false, update after the value") {
      val uniqueIndexName = UniqueIndexName("index")

      val columnNameSet1 = Set(ColumnName("column1"))
      val uniqueIndex1 = UniqueIndex(Map(uniqueIndexName -> columnNameSet1))
      val columnNameSet2 = Set(ColumnName("column2"))
      val uniqueIndex2 = UniqueIndex(Map(uniqueIndexName -> columnNameSet2))

      uniqueIndex1.merge(
        uniqueIndex2,
        checkUnique = false
      ) shouldBe uniqueIndex2
    }
  }

  describe("toSqlSentenceView") {
    it("success") {
      val uniqueIndexName = UniqueIndexName("index")
      val columnNameSet = Set(ColumnName("column1"), ColumnName("column2"))
      val uniqueIndex = UniqueIndex(Map(uniqueIndexName -> columnNameSet))

      uniqueIndex.toSqlSentenceView.toSeq shouldBe List(
        "UNIQUE INDEX (column1, column2)"
      )
    }
  }
}
