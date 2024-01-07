package utils.edge

import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EdgeQuerySpec extends AnyFunSpec with Matchers {
  describe("countAll") {
    it("get the number of all vertices") {
      val graph = TinkerFactory.createModern().traversal()
      val edgeQuery = EdgeQuery(graph)
      edgeQuery.countAll shouldBe 6
    }
  }
}
