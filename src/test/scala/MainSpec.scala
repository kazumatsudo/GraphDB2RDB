import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.Database
import usecase.{ByExhaustiveSearch, UsecaseBase, UsingSpecificKeyList}
import utils.Config

class MainSpec extends AsyncFunSpec with Matchers {

  sealed trait DatabaseType
  case object DatabaseTypeH2 extends DatabaseType
  case object DatabaseTypeMysql extends DatabaseType

  private val databaseH2 = Database.forConfig("database-h2")
  private val databaseMysql = Database.forConfig("database-mysql")

  private def initalizeDb(databaseType: DatabaseType, database: Database) =
    databaseType match {
      case DatabaseTypeH2 =>
        for {
          result <- database.run(sqlu"DROP ALL OBJECTS DELETE FILES;")
        } yield result
      case DatabaseTypeMysql =>
        for {
          // setup - initialize db
          tableNameList <- database.run(sql"show tables;".as[String])
          result <- database.run(
            DBIO.sequence(
              Seq(sqlu"SET FOREIGN_KEY_CHECKS = 0;") ++ tableNameList.map(
                tableName => sqlu"DROP TABLE IF EXISTS #$tableName;"
              ) ++ Seq(sqlu"SET FOREIGN_KEY_CHECKS = 1;")
            )
          )
        } yield result
    }

  private def assert(
      databaseType: DatabaseType,
      database: Database,
      usecase: UsecaseBase
  ) = {
    val result = for {
      // setup - initialize db
      _ <- initalizeDb(databaseType, database)

      // execute
      usecaseResponse <- usecase.execute(checkUnique = true)
      result <- database.run(
        DBIO.sequence(
          usecaseResponse.verticesDdl.toSqlSentence.map(sql =>
            sqlu"#$sql"
          ) ++ usecaseResponse.edgesDdl.toSqlSentence.map(sql =>
            sqlu"#$sql"
          ) ++ usecaseResponse.verticesDml.toSqlSentence.map(sql =>
            sqlu"#$sql"
          ) ++ usecaseResponse.edgesDml.toSqlSentence.map(sql => sqlu"#$sql")
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

  describe("enable to execute in") {
    val config = Config.default
    val (g, request) =
      GenerateTestData.generate(TinkerGraph.open().traversal(), 100, 5, 5)

    describe("H2") {
      it("ByExhaustiveSearch") {
        assert(DatabaseTypeH2, databaseH2, ByExhaustiveSearch(g, config))
      }

      // TODO: https://github.com/kazumatsudo/GraphDB2RDB/issues/73
      it("UsingSpecificKeyList") {
        assert(
          DatabaseTypeH2,
          databaseH2,
          UsingSpecificKeyList(g, config, request)
        )
      }
    }

    describe("MySQL") {
      it("ByExhaustiveSearch") {
        assert(DatabaseTypeMysql, databaseMysql, ByExhaustiveSearch(g, config))
      }

      // TODO: https://github.com/kazumatsudo/GraphDB2RDB/issues/73
      it("UsingSpecificKeyList") {
        assert(
          DatabaseTypeMysql,
          databaseMysql,
          UsingSpecificKeyList(g, config, request)
        )
      }
    }
  }
}
