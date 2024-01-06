package utils

import gremlin.scala.GremlinScala
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class VertexUtilitiesSpec extends AnyFunSpec with Matchers {
  describe("countAll") {
    it ("get the number of all vertices") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexUtilities = VertexUtilities(graph)
      vertexUtilities.countAll shouldBe 6
    }
  }

  describe("getVerticesList") {
    describe("require"){
      it ("start must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val vertexUtilities = VertexUtilities(graph)
        intercept[IllegalArgumentException] {
          vertexUtilities.getVerticesList(-1, 0)
        }
      }

      it ("count must be positive.") {
        val graph = TinkerFactory.createModern().traversal()
        val vertexUtilities = VertexUtilities(graph)
        intercept[IllegalArgumentException] {
          vertexUtilities.getVerticesList(0, -1)
        }
      }
    }

    it ("get the number of all vertices") {
      val graph = TinkerFactory.createModern().traversal()
      val vertexUtilities = VertexUtilities(graph)
      vertexUtilities.getVerticesList(0, 1) shouldBe Seq(GremlinScala(graph.V()).head())
    }
  }
}
