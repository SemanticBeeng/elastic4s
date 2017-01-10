package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class RawQueryTest extends WordSpec with ElasticSugar with Matchers {

  client.execute {
    bulk(
      index into "rawquerytest/paris" fields ("landmark" -> "montmarte", "arrondissement" -> "18"),
      index into "rawquerytest/paris" fields ("landmark" -> "le tower eiffel", "arrondissement" -> "7")
    )
  }.await

  blockUntilCount(2, "rawquerytest")

  "raw query" should {
    "work!" in {
      val hits = client.execute {
        search in "*" types ("rawquerytest", "paris") limit 5 rawQuery {
          """{ "prefix": { "landmark": { "prefix": "montm" } } }"""
        }
      }.await.getHits.totalHits
      assert(hits === 1)
    }
  }
}
