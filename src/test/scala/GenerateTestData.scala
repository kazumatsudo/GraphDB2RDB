import com.typesafe.scalalogging.StrictLogging
import faker.Faker
import gremlin.scala.Vertex
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoMapper
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerIoRegistryV3d0
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry
import usecase.{
  UsingSpecificKeyListRequest,
  UsingSpecificKeyListRequestKey,
  UsingSpecificKeyListRequestLabel
}
import utils.{Config, FileUtility, JsonUtility}

import java.util.UUID
import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.control.NonFatal
import scala.util.{Random, Using}

object GenerateTestData extends StrictLogging {

  private val config = Config.default
  // TODO: use Faker.all
  private val faker = Faker.ja

  private val allCharacters =
    (0 to math.pow(2, 16).toInt).view.map(_.toChar).mkString
  private def sqlInjectionSentence(tableName: String) =
    s"'); DROP TABLE ${config.tableName.vertex}_${tableName}; --;"

  /* generate vertices */

  private def generateVertexAddress(g: GraphTraversalSource) = g
    .addV("address")
    .property("addressId", UUID.randomUUID())
    .property("buildingNumber", faker.buildingNumber)
    .property("city", faker.city)
    .property("state", faker.state.name)
    .property("postalCode", faker.stateZip)
    .property("createdAt", faker.currentEraInstant())
    .next()

  private def generateVertexCompany(g: GraphTraversalSource) = {
    val hasWebsite = Random.nextBoolean()

    val vertex = g
      .addV("company")
      .property("companyId", UUID.randomUUID())
      .property("name", faker.companyName)
      .property("phoneNumber", faker.phoneNumber)

    if (hasWebsite) {
      vertex.property("url", faker.companyUrl)
    }

    vertex.property("createdAt", faker.currentEraInstant())
    vertex.next()
  }

  private def generateVertexPerson(
      g: GraphTraversalSource,
      lastName: String,
      age: Int
  ) = {
    val hasCellPhone = Random.nextBoolean()

    val vertex = g
      .addV("person")
      .property("personId", UUID.randomUUID())
      .property("firstName", faker.firstName)
      .property("lastName", lastName)
      .property("age", age)

    if (hasCellPhone) {
      vertex
        .property("phoneNumber", faker.phoneNumber)
        .property("emailAddress", faker.emailAddress)
    }

    vertex.property("createdAt", faker.currentEraInstant())
    vertex.next()
  }

  private def generateVertexPokemon(g: GraphTraversalSource) = g
    .addV("pokemon")
    .property("pokemonId", UUID.randomUUID())
    .property("pokemonName", faker.pokemonName)
    .property("pokemonLocation", faker.pokemonLocation)
    .property("pokemonMove", faker.pokemonMove)
    .property("createdAt", faker.currentEraInstant())
    .next()

  private def generateVertexSchool(g: GraphTraversalSource) = {
    val hasWebsite = Random.nextBoolean()

    val vertex = g
      .addV("school")
      .property("schoolId", UUID.randomUUID())
      .property("name", s"${faker.streetName} school")
      .property("phoneNumber", faker.phoneNumber)

    if (hasWebsite) {
      vertex.property("url", faker.url)
    }

    vertex.property("createdAt", faker.currentEraInstant())
    vertex.next()
  }

  private def generateVertexForSqlTest(g: GraphTraversalSource) = {
    val tableName = "vertexForSql"

    g
      .addV(tableName)
      .property("charactersNeedToEscape", allCharacters)
      .property("sqlInjectionSentence", sqlInjectionSentence(tableName))
      .next()
  }

  /* generate edges */

  private def connectEdgeBelongTo(
      g: GraphTraversalSource,
      from: Vertex,
      to: Vertex
  ) = g
    .addE("belongTo")
    .from(from)
    .to(to)
    .property("belongToId", UUID.randomUUID())
    .property("createdAt", faker.currentEraInstant())
    .next()

  private def connectEdgeBreedPokemonTo(
      g: GraphTraversalSource,
      from: Vertex,
      to: Vertex
  ) = {
    val wantPokemonToLearn = Random.nextBoolean()

    val edge = g
      .addE("breedPokemon")
      .from(from)
      .to(to)
      .property("breedPokemonId", UUID.randomUUID())
      .property("caught", faker.pokemonLocation())
      .property("createdAt", faker.currentEraInstant())

    if (wantPokemonToLearn) {
      edge
        .property("wantPokemonToLearn", faker.pokemonMove())
    }

    edge.next()
  }

  private def connectEdgeParent(
      g: GraphTraversalSource,
      from: Vertex,
      to: Vertex
  ) = g
    .addE("parent")
    .from(from)
    .to(to)
    .property("parentId", UUID.randomUUID())
    .next()

  private def connectEdgeLive(
      g: GraphTraversalSource,
      from: Vertex,
      to: Vertex
  ) = g
    .addE("live")
    .from(from)
    .to(to)
    .property("edgeId", UUID.randomUUID())
    .next()

  private def connectEdgeLocation(
      g: GraphTraversalSource,
      from: Vertex,
      to: Vertex
  ) = g
    .addE("location")
    .from(from)
    .to(to)
    .property("locationId", UUID.randomUUID())
    .next()

  private def connectEdgeForSqlTest(
      g: GraphTraversalSource,
      from: Vertex,
      to: Vertex
  ) = {
    val tableName = "edgeForSql"
    g
      .addE(tableName)
      .from(from)
      .to(to)
      .property("charactersNeedToEscape", allCharacters)
      .property("sqlInjectionSentence", sqlInjectionSentence(tableName))
      .next()
  }

  def generate(
      g: GraphTraversalSource,
      personCount: Int,
      companyCount: Int,
      schoolCount: Int
  ): (GraphTraversalSource, UsingSpecificKeyListRequest) = {
    logger.info("[ 1/ 8] start : generate person Vertices")

    val verticesPerson =
      (0 until personCount)
        .map(_ => generateVertexPerson(g, faker.lastName, Random.nextInt(60)))
        .flatMap { person =>
          val lastName = person.value[String]("lastName")
          val age = person.value[Int]("age")

          // a person has a parent
          val parent = generateVertexPerson(
            g,
            lastName,
            Random.between(age + 25, age + 35)
          )
          connectEdgeParent(g, person, parent)

          // a person may have children
          val children = if (age >= 30) {
            val childrenCount = Random.nextInt(4)
            (0 until childrenCount).map { _ =>
              val child = generateVertexPerson(
                g,
                lastName,
                Math.max(Random.between(age - 35, age - 25), 0)
              )

              connectEdgeParent(g, child, parent)
              child
            }
          } else {
            Seq.empty
          }

          children :+ person :+ parent
        }

    logger.info("[ 1/ 8] finish: generate person Vertices")
    logger.info("[ 2/ 8] start : generate company Vertices")

    val verticesCompany =
      (0 until companyCount).map(_ => generateVertexCompany(g))

    logger.info("[ 2/ 8] finish: generate company Vertices")
    logger.info("[ 3/ 8] start : generate school Vertices")

    val verticesSchool =
      (0 until schoolCount).map(_ => generateVertexSchool(g))

    logger.info("[ 3/ 8] finish: generate school Vertices")
    logger.info("[ 4/ 8] start : generate person edges")

    verticesPerson.foreach { person =>
      // a person has one address
      connectEdgeLive(g, person, generateVertexAddress(g))

      // a person may belongs to a school or company
      val belongsVertexOption = person.value[Int]("age") match {
        case age if 6 <= age && age <= 18 =>
          val randomIndex = Random.nextInt(verticesSchool.length)
          verticesSchool.lift(randomIndex)
        case age if 18 < age && age <= 60 =>
          val randomIndex = Random.nextInt(verticesCompany.length)
          verticesCompany.lift(randomIndex)
        case _ => None
      }
      belongsVertexOption.map(connectEdgeBelongTo(g, person, _))

      // a person may love a certain Pokemon
      val likePokemon = Random.nextBoolean()
      if (likePokemon) {
        connectEdgeBreedPokemonTo(g, person, generateVertexPokemon(g))
      }
    }

    logger.info("[ 4/ 8] finish: generate person edges")
    logger.info("[ 5/ 8] start : generate company edges")

    verticesCompany.foreach { company =>
      // a company has one address
      connectEdgeLocation(g, company, generateVertexAddress(g))
    }

    logger.info("[ 5/ 8] finish: generate company edges")
    logger.info("[ 6/ 8] start : generate school edges")

    verticesSchool.foreach { school =>
      // a school has one address
      connectEdgeLocation(g, school, generateVertexAddress(g))
    }

    logger.info("[ 6/ 8] finish: generate school edges")
    logger.info("[ 7/ 8] start : generate test data for SQL")

    // not include for usingSpecificKeyListRequest
    connectEdgeForSqlTest(
      g,
      generateVertexForSqlTest(g),
      generateVertexForSqlTest(g)
    )

    logger.info("[ 7/ 8] finish: generate test data for SQL")
    logger.info(
      "[ 8/ 8] start : generate using specific key list request json"
    )

    val usingSpecificKeyListRequest = UsingSpecificKeyListRequest(
      Seq(
        UsingSpecificKeyListRequestLabel(
          "person",
          Seq(
            UsingSpecificKeyListRequestKey(
              "personId",
              verticesPerson.map(_.value[UUID]("personId"))
            )
          )
        ),
        UsingSpecificKeyListRequestLabel(
          "company",
          Seq(
            UsingSpecificKeyListRequestKey(
              "companyId",
              verticesCompany.map(_.value[UUID]("companyId"))
            )
          )
        ),
        UsingSpecificKeyListRequestLabel(
          "school",
          Seq(
            UsingSpecificKeyListRequestKey(
              "schoolId",
              verticesSchool.map(_.value[UUID]("schoolId"))
            )
          )
        )
      )
    )

    logger.info(
      "[ 8/ 8] finish: generate using specific key list request json"
    )

    (g, usingSpecificKeyListRequest)
  }

  /** @param args
    */
  def main(args: Array[String]): Unit = {
    val grapdbConnection = config.graphDb.remoteGraphProperties

    {
      logger.warn(s"This process stores test data on the gremlin server.")
      logger.warn(s"Check the config file of the connection.")
      logger.warn(s"config file: $grapdbConnection")

      @tailrec
      def checkInput(): Unit = {
        logger.info("Enter [y/n]: ")

        StdIn.readLine().trim match {
          case "y" => ()
          case "n" =>
            logger.info("suspend the process.")
            sys.exit(1)
          case invalidInput =>
            logger.warn(s"invalid Input: $invalidInput")
            checkInput()
        }
      }

      checkInput()
    }

    Using(
      {
        val serializer = new GryoMessageSerializerV3d0(
          GryoMapper.build
            .addRegistry(JanusGraphIoRegistry.instance())
            .addRegistry(TinkerIoRegistryV3d0.instance())
        )
        val cluster = Cluster.build
          .addContactPoint("localhost")
          .port(8182)
          .serializer(serializer)
          //          .maxConnectionPoolSize(81928192)
          .create()

        traversal().withRemote(DriverRemoteConnection.using(cluster))
        //        traversal().withRemote(config.getString("graphdb_remote_graph_properties"))
      }
    ) { g =>
      val (_, usingSpecificKeyListRequest) = generate(g, 100, 5, 5)

      FileUtility.writeJson(
        config.usingSpecificKeyList.outputDirectory,
        config.usingSpecificKeyList.filename,
        JsonUtility.writeForUsingSpecificKeyListRequest(
          usingSpecificKeyListRequest
        )
      )
    }.recover { case NonFatal(e) =>
      logger.error(s"${e.getMessage}", e)
      sys.exit(1)
    }
  }
}
