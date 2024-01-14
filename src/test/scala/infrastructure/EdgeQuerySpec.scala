package infrastructure

import domain.graph.{GraphEdge, GraphVertex}
import gremlin.scala.GremlinScala
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EdgeQuerySpec extends AnyFunSpec with Matchers {
  describe("countAll") {
    it("get the number of all edges") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph)
      edgeQuery.countAll shouldBe 6
    }
  }

  describe("getInEdgeList") {
    it("get inE list") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph)
      val edgeList = GremlinScala(graph.E()).toList().map(GraphEdge)
      edgeQuery.getInEdgeList(
        GraphVertex(GremlinScala(graph.V()).toList()(1))
      ) shouldBe Seq(
        edgeList.head
      )
    }
  }

  describe("getList") {
    describe("require") {
      it("start must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val edgeQuery = EdgeQuery(graph)
        intercept[IllegalArgumentException] {
          edgeQuery.getList(-1, 0)
        }
      }

      it("count must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val edgeQuery = EdgeQuery(graph)
        intercept[IllegalArgumentException] {
          edgeQuery.getList(-1, 0)
        }
      }
    }

    it("get the number of all edges") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph)
      edgeQuery.getList(0, 1) shouldBe Seq(
        GraphEdge(GremlinScala(graph.E()).head())
      )
    }
  }
}
