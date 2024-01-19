package infrastructure

import domain.graph.{GraphEdge, GraphVertex}
import gremlin.scala.GremlinScala
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import utils.Config

class EdgeQuerySpec extends AsyncFunSpec with Matchers {
  describe("countAll") {
    it("get the number of all edges") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, Config.default)
      edgeQuery.countAll.map(_ shouldBe 6)
    }
  }

  describe("getInEdgeList") {
    it("get inE list") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, Config.default)
      val edgeList =
        GremlinScala(graph.E()).toList().map(GraphEdge(_, Config.default))
      edgeQuery
        .getInEdgeList(
          GraphVertex(GremlinScala(graph.V()).toList()(1), Config.default)
        )
        .map { result => result.toSeq shouldBe Seq(edgeList.head) }
    }
  }

  describe("getList") {
    describe("require") {
      it("start must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val edgeQuery = EdgeQuery(graph, Config.default)
        recoverToSucceededIf[IllegalArgumentException] {
          edgeQuery.getList(-1, 0)
        }
      }

      it("count must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val edgeQuery = EdgeQuery(graph, Config.default)
        recoverToSucceededIf[IllegalArgumentException] {
          edgeQuery.getList(0, -1)
        }
      }
    }

    it("get the number of all edges") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, Config.default)
      edgeQuery.getList(0, 1).map {
        _.toSeq shouldBe Seq(
          GraphEdge(GremlinScala(graph.E()).head(), Config.default)
        )
      }
    }
  }

  describe("getOutEdgeList") {
    it("get outE list") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph, Config.default)
      val edgeList =
        GremlinScala(graph.E()).toList().map(GraphEdge(_, Config.default))
      edgeQuery
        .getOutEdgeList(GraphVertex(graph.V().next(), Config.default))
        .map { result =>
          result.toSeq shouldBe Seq(edgeList(2), edgeList.head, edgeList(1))
        }
    }
  }
}
