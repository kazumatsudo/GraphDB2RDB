import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.scalatest.Assertion
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.Database
import usecase.{ByExhaustiveSearch, UsecaseBase}
import utils.Config

import scala.concurrent.Future

class MainSpec extends AsyncFunSpec with Matchers {

  private val database = Database.forConfig("database-mysql")

  describe("enable to execute in") {
    val config = Config.default
    val (g, _) =
      GenerateTestData.generate(TinkerGraph.open().traversal(), 100, 5, 5)

    def assert(database: Database, usecase: UsecaseBase): Future[Assertion] = {
      val result = for {
        // setup - initialize db
        tableNameList <- database.run(sql"show tables;".as[String])
        _ <- database.run(
          DBIO.sequence(
            Seq(sqlu"SET FOREIGN_KEY_CHECKS = 0;") ++ tableNameList.map(
              tableName => sqlu"DROP TABLE IF EXISTS #${tableName};"
            ) ++ Seq(sqlu"SET FOREIGN_KEY_CHECKS = 1;")
          )
        )

        // execute
        usecaseResponse <- usecase.execute(checkUnique = true)
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

    describe("MySQL") {
      it("ByExhaustiveSearch") {
        assert(database, ByExhaustiveSearch(g, config))
      }

      // TODO: analyze inVertex and outVertex DDL/DML
      //      it("UsingSpecificKeyList") {
      //        assert(database, UsingSpecificKeyList(g, config, request))
      //      }
    }
  }
}
