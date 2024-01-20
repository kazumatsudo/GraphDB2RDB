import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import faker.Faker
import gremlin.scala.Vertex
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import usecase.{
  UsingSpecificKeyListRequest,
  UsingSpecificKeyListRequestKey,
  UsingSpecificKeyListRequestLabel
}
import utils.{FileUtility, JsonUtility}

import scala.util.{Random, Using}

object GenerateTestData extends StrictLogging {

  private val faker = Faker.default

  /* generate vertices */

  private def generateVertexAddress(g: GraphTraversalSource) = g
    .addV("address")
    .property("addressId", Random.nextInt(Int.MaxValue))
    .property("buildingNumber", faker.buildingNumber)
    .property("city", faker.city)
    .property("state", faker.state.name)
    .property("postalCode", faker.stateZip)
    .next()

  private def generateVertexCompany(g: GraphTraversalSource) = {
    val hasWebsite = Random.nextBoolean()

    val vertex = g
      .addV("company")
      .property("companyId", Random.nextInt(Int.MaxValue))
      .property("name", faker.companyName)
      .property("phoneNumber", faker.phoneNumber)

    if (hasWebsite) {
      vertex.property("url", faker.companyUrl)
    }

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
      .property("personId", Random.nextInt(Int.MaxValue))
      .property("firstName", faker.firstName)
      .property("lastName", lastName)
      .property("age", age)

    if (hasCellPhone) {
      vertex
        .property("phoneNumber", faker.phoneNumber)
        .property("emailAddress", faker.emailAddress)
    }

    vertex.next()
  }

  private def generateVertexPokemon(g: GraphTraversalSource) = g
    .addV("pokemon")
    .property("pokemonId", Random.nextInt(Int.MaxValue))
    .property("pokemonName", faker.pokemonName)
    .property("pokemonLocation", faker.pokemonLocation)
    .property("pokemonMove", faker.pokemonMove)
    .next()

  private def generateVertexSchool(g: GraphTraversalSource) = {
    val hasWebsite = Random.nextBoolean()

    val vertex = g
      .addV("school")
      .property("schoolId", Random.nextInt(Int.MaxValue))
      .property("name", s"${faker.streetName} school")
      .property("phoneNumber", faker.phoneNumber)

    if (hasWebsite) {
      vertex.property("url", faker.url)
    }
    vertex.next()
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
    .property("belongToId", Random.nextInt(Int.MaxValue))
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
      .property("breedPokemonId", Random.nextInt(Int.MaxValue))
      .property("caught", faker.pokemonLocation())

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
    .property("parentId", Random.nextInt(Int.MaxValue))
    .next()

  private def connectEdgeLive(
      g: GraphTraversalSource,
      from: Vertex,
      to: Vertex
  ) = g
    .addE("live")
    .from(from)
    .to(to)
    .property("edgeId", Random.nextInt(Int.MaxValue))
    .next()

  private def connectEdgeLocation(
      g: GraphTraversalSource,
      from: Vertex,
      to: Vertex
  ) = g
    .addE("location")
    .from(from)
    .to(to)
    .property("locationId", Random.nextInt(Int.MaxValue))
    .next()

  /** @param args
    */
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    Using(
      traversal().withRemote(
        config.getString("graphdb_remote_graph_properties")
      )
    ) { g =>
      val personCount = 5
      val companyCount = 5
      val schoolCount = 5

      logger.info("[ 1/ 6] start : generate person Vertices")

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

      logger.info("[ 1/ 6] finish: generate person Vertices")
      logger.info("[ 2/ 6] start : generate company Vertices")

      val verticesCompany =
        (0 until companyCount).map(_ => generateVertexCompany(g))

      logger.info("[ 2/ 6] finish: generate company Vertices")
      logger.info("[ 3/ 6] start : generate school Vertices")

      val verticesSchool =
        (0 until schoolCount).map(_ => generateVertexSchool(g))

      logger.info("[ 3/ 6] finish: generate company Vertices")
      logger.info("[ 4/ 6] start : generate person edges")

      verticesPerson.foreach { person =>
        // a person has one address
        connectEdgeLive(g, person, generateVertexAddress(g))

        // a person may belongs to a school or company
        val belongsVertexOption = person.value[Int]("age") match {
          case age if 6 <= age && age <= 18 =>
            val randomIndex = Random.nextInt(verticesSchool.length)
            Some(verticesSchool(randomIndex))
          case age if 18 < age && age <= 60 =>
            val randomIndex = Random.nextInt(verticesCompany.length)
            Some(verticesCompany(randomIndex))
          case _ => None
        }
        belongsVertexOption.map(connectEdgeBelongTo(g, person, _))

        // a person may love a certain Pokemon
        val likePokemon = Random.nextBoolean()
        if (likePokemon) {
          connectEdgeBreedPokemonTo(g, person, generateVertexPokemon(g))
        }
      }

      logger.info("[ 4/ 6] finish: generate person edges")
      logger.info("[ 5/ 6] start : generate person companies")

      verticesCompany.foreach { company =>
        // a company has one address
        connectEdgeLocation(g, company, generateVertexAddress(g))
      }

      logger.info("[ 5/ 6] finish: generate person companies")
      logger.info(
        "[ 6/ 6] start : generate using specific key list request json"
      )

      val usingSpecificKeyListRequest = UsingSpecificKeyListRequest(
        Seq(
          UsingSpecificKeyListRequestLabel(
            "person",
            Seq(
              UsingSpecificKeyListRequestKey(
                "personId",
                verticesPerson.map(_.value[Int]("personId"))
              )
            )
          ),
          UsingSpecificKeyListRequestLabel(
            "company",
            Seq(
              UsingSpecificKeyListRequestKey(
                "companyId",
                verticesCompany.map(_.value[Int]("companyId"))
              )
            )
          ),
          UsingSpecificKeyListRequestLabel(
            "school",
            Seq(
              UsingSpecificKeyListRequestKey(
                "schoolId",
                verticesSchool.map(_.value[Int]("schoolId"))
              )
            )
          )
        )
      )
      val jsonString = JsonUtility.writeForUsingSpecificKeyListRequest(
        usingSpecificKeyListRequest
      )
      FileUtility.writeJson("using_key_list_file", jsonString)

      logger.info(
        "[ 6/ 6] finish: generate using specific key list request json"
      )
    }
  }
}
