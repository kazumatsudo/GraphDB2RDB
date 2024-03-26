package infrastructure

import domain.graph.{GraphEdge, GraphVertex}
import gremlin.scala.GremlinScala
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import utils.Config

class VertexQuerySpec extends AsyncFunSpec with Matchers {
  private val config = Config.default

  describe("countAll") {
    it("get the number of all vertices") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph, config)
      vertexQuery.countAll.map { _ shouldBe 6 }
    }
  }

  describe("getInVertexList") {
    it("get inV list") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph, config)
      vertexQuery
        .getInVertexList(
          GraphEdge(
            GremlinScala(graph.E()).toList().headOption.get,
            config,
            graph
          )
        )
        .map {
          _.toSeq shouldBe Seq(
            GremlinScala(graph.V())
              .toList()
              .lift(1)
              .map(GraphVertex(_, config, graph))
          ).flatten
        }
    }
  }

  describe("getList") {
    describe("require") {
      it("start must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val vertexQuery = VertexQuery(graph, config)
        recoverToSucceededIf[IllegalArgumentException] {
          vertexQuery.getList(-1, 0)
        }
      }

      it("count must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val vertexQuery = VertexQuery(graph, config)
        recoverToSucceededIf[IllegalArgumentException] {
          vertexQuery.getList(0, -1)
        }
      }
    }

    it("get the number of all vertices") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph, config)
      vertexQuery.getList(0, 1).map {
        _.toSeq shouldBe Seq(
          GraphVertex(GremlinScala(graph.V()).head(), config, graph)
        )
      }
    }
  }

  describe("getListByPropertyKey") {
    describe("require") {
      it("label must not be empty.") {
        val graph = TinkerFactory.createModern().traversal()
        val vertexQuery = VertexQuery(graph, config)
        recoverToSucceededIf[IllegalArgumentException] {
          vertexQuery.getListByPropertyKey("", "key", "value")
        }
      }

      it("key must not be empty.") {
        val graph = TinkerFactory.createModern().traversal()
        val vertexQuery = VertexQuery(graph, config)
        recoverToSucceededIf[IllegalArgumentException] {
          vertexQuery.getListByPropertyKey("label", "", "value")
        }
      }
    }

    it("get vertices") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph, config)
      vertexQuery
        .getListByPropertyKey("person", "name", "marko")
        .map {
          _.toSeq shouldBe Seq(
            GraphVertex(GremlinScala(graph.V()).head(), config, graph)
          )
        }
    }
  }

  describe("getOutVertexList") {
    it("get outV list") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexQuery = VertexQuery(graph, config)
      vertexQuery
        .getOutVertexList(
          GraphEdge(
            GremlinScala(graph.E()).toList().headOption.get,
            config,
            graph
          )
        )
        .map {
          _.toSeq shouldBe Seq(
            GremlinScala(graph.V())
              .toList()
              .headOption
              .map(GraphVertex(_, config, graph))
          ).flatten
        }
    }
  }
}
