package utils

import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class VertexUtilitiesSpec extends AnyFunSpec with Matchers {
  describe("countAll") {
    it ("get the number of all vertices") {
      val graph = TinkerFactory.createModern()
      val vertexUtilities = VertexUtilities(graph.traversal())
      vertexUtilities.countAll shouldBe 6
    }
  }
}
