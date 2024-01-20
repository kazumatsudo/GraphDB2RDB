package utils

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ConfigSpec extends AnyFunSpec with Matchers {
  describe("default") {
    it("success") {
      Config.default shouldBe Config(
        AnalysysMethod(
          "by_exhaustive_search",
          "src/main/resources/using_key_list_file.json"
        ),
        UsingSpecificKeyList("json", "using_key_list_file"),
        GraphDb("conf/remote-graph.properties"),
        Sql("ddl_edge", "ddl_vertex", "dml_edge", "dml_vertex", "sql"),
        TableName("edge", "vertex"),
        ColumnName("id", "id_in_v", "id_out_v", "id", "property_")
      )
    }
  }
}
