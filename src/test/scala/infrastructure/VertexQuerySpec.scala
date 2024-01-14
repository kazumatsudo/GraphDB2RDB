package infrastructure

import domain.graph.GraphVertex
import gremlin.scala.GremlinScala
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class VertexQuerySpec extends AnyFunSpec with Matchers {
  describe("countAll") {
    it("get the number of all vertices") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      vertexQuery.countAll shouldBe 6
    }
  }

  describe("getList") {
    describe("require") {
      it("start must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val vertexQuery = VertexQuery(graph)
        intercept[IllegalArgumentException] {
          vertexQuery.getList(-1, 0)
        }
      }

      it("count must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val vertexQuery = VertexQuery(graph)
        intercept[IllegalArgumentException] {
          vertexQuery.getList(0, -1)
        }
      }
    }

    it("get the number of all vertices") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      vertexQuery.getList(0, 1) shouldBe Seq(
        GraphVertex(GremlinScala(graph.V()).head())
      )
    }
  }

  describe("getListByPropertyKey") {
    describe("require") {
      it("label must not be empty.") {
        val graph = TinkerFactory.createModern().traversal()
        val vertexQuery = VertexQuery(graph)
        intercept[IllegalArgumentException] {
          vertexQuery.getListByPropertyKey("", "key", "value")
        }
      }

      it("key must not be empty.") {
        val graph = TinkerFactory.createModern().traversal()
        val vertexQuery = VertexQuery(graph)
        intercept[IllegalArgumentException] {
          vertexQuery.getListByPropertyKey("label", "", "value")
        }
      }
    }

    it("get vertices") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph)
      vertexQuery.getListByPropertyKey("person", "name", "marko") shouldBe Seq(
        GraphVertex(GremlinScala(graph.V()).head())
      )
    }
  }
}
