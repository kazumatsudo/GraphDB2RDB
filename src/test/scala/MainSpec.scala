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

      val result = for {
        usecaseResponse <- usecase.execute(checkUnique = true)
        database = Database.forConfig("database-h2")
        result <- database.run(
          DBIO.sequence(
            usecaseResponse.verticesDdl.toSqlSentence
              .map(sql => sqlu"#$sql")
              ++
                usecaseResponse.edgesDdl.toSqlSentence
                  .map(sql => sqlu"#$sql")
                ++
                usecaseResponse.verticesDml.toSqlSentence
                  .map(sql => sqlu"#$sql")
                ++
                usecaseResponse.edgesDml.toSqlSentence
                  .map(sql => sqlu"#$sql")
          )
        )
      } yield result

      result
        .map { _ => succeed }
        .recover { case e =>
          fail(
            s"usecase#execute returns None. e: ${e.getMessage}",
            e
          )
        }
    }
  }
}
