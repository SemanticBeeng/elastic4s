package com.sksamuel.elastic4s.search.aggs

class MinAggregationTest extends AbstractAggregationTest {

  "min aggregation" - {
    "should count min value for field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation min "agg1" field "age"
        }
      }.await
      resp.totalHits shouldBe 10
      val aggs = resp.aggregations.minResult("agg1")
      aggs.getValue shouldBe 26
    }
  }
}