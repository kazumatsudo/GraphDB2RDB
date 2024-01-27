import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.scalatest.Assertion
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.Database
import usecase.{ByExhaustiveSearch, UsecaseBase, UsingSpecificKeyList}
import utils.Config

import scala.concurrent.Future

class MainSpec extends AsyncFunSpec with Matchers {

  private val database = Database.forConfig("database-mysql")
  private val databaseName = "graphdb2rdb"

  describe("enable to execute in") {
    val config = Config.default
    val (g, request) =
      GenerateTestData.generate(TinkerGraph.open().traversal(), 100, 5, 5)

    def assert(database: Database, usecase: UsecaseBase): Future[Assertion] = {
      val result = for {
        usecaseResponse <- usecase.execute(checkUnique = true)
        // setup
        _ <- database.run(sqlu"CREATE DATABASE IF NOT EXISTS #${databaseName};")

        // execute
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

        // teardown
        _ <- database.run(sqlu"CREATE DATABASE IF NOT EXISTS #${databaseName};")
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

    describe("MySQL") {
      it("ByExhaustiveSearch") {
        assert(database, ByExhaustiveSearch(g, config))
      }

      it("UsingSpecificKeyList") {
        assert(database, UsingSpecificKeyList(g, config, request))
      }
    }
  }
}
