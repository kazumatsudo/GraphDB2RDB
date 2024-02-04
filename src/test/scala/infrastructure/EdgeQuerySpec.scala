package infrastructure

import domain.graph.{GraphEdge, GraphVertex}
import gremlin.scala.GremlinScala
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import utils.Config

class EdgeQuerySpec extends AsyncFunSpec with Matchers {
  private val config = Config.default

  describe("countAll") {
    it("get the number of all edges") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, config)
      edgeQuery.countAll.map { _ shouldBe 6 }
    }
  }

  describe("getInEdgeList") {
    it("get inE list") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, config)
      val edgeList = GremlinScala(graph.E()).toList().map(GraphEdge(_, config))
      edgeQuery
        .getInEdgeList(
          GraphVertex(GremlinScala(graph.V()).toList().lift(1).get, config)
        )
        .map { _.toSeq shouldBe Seq(edgeList.head) }
    }
  }

  describe("getList") {
    describe("require") {
      it("start must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val edgeQuery = EdgeQuery(graph, config)
        recoverToSucceededIf[IllegalArgumentException] {
          edgeQuery.getList(-1, 0)
        }
      }

      it("count must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val edgeQuery = EdgeQuery(graph, config)
        recoverToSucceededIf[IllegalArgumentException] {
          edgeQuery.getList(-1, 0)
        }
      }
    }

    it("get the number of all edges") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, config)
      edgeQuery.getList(0, 1).map {
        _.toSeq shouldBe Seq(
          GraphEdge(GremlinScala(graph.E()).head(), config)
        )
      }
    }
  }

  describe("getOutEdgeList") {
    it("get outE list") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, config)
      val edgeList = GremlinScala(graph.E()).toList().map(GraphEdge(_, config))
      edgeQuery
        .getOutEdgeList(
          GraphVertex(graph.V().next(), config)
        )
        .map {
          _.toSeq shouldBe Seq(
            edgeList.lift(2),
            edgeList.headOption,
            edgeList.lift(1)
          ).flatten
        }
    }
  }
}
