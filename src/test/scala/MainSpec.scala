import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.Database
import usecase.ByExhaustiveSearch
import utils.Config

class MainSpec extends AsyncFunSpec with Matchers {
  private val config = Config.default

  describe("enable to execute in") {
    it("H2") {
      val graph = TinkerFactory.createModern().traversal()
      val usecase = ByExhaustiveSearch(graph, config)
      val (
        verticesDdlResult,
        verticesDmlResult,
        edgesDdlResult,
        edgesDmlResult
      ) = usecase.execute(checkUnique = true)

      val result = for {
        verticesDdl <- verticesDdlResult
        verticesDml <- verticesDmlResult
        edgesDdl <- edgesDdlResult
        edgesDml <- edgesDmlResult
      } yield {
        val database = Database.forConfig("database-h2")
        database.run(
          DBIO.sequence(
            verticesDdl.toSqlSentence.map(sql => sqlu"#$sql").toSeq ++
              edgesDdl.toSqlSentence.map(sql => sqlu"#$sql").toSeq ++
              verticesDml.toSqlSentence.map(sql => sqlu"#$sql").toSeq ++
              edgesDml.toSqlSentence.map(sql => sqlu"#$sql").toSeq
          )
        )
      }

      result match {
        case Some(value) => value.map { _ => succeed }
        case None =>
          fail(
            s"usecase#execute returns None. verticesDdlResult: $verticesDdlResult, verticesDmlResult: $verticesDmlResult, edgesDdlResult: $edgesDdlResult, edgesDmlResult: $edgesDmlResult"
          )
      }
    }
  }
}
