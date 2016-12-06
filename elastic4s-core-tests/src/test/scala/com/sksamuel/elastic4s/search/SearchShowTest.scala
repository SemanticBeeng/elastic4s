package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class SearchShowTest extends WordSpec with Matchers with ElasticSugar {

  "Search" should {
    "have a show typeclass implementation" in {

      val request = {
        search in "gameofthrones" / "characters" query {
          bool {
            should {
              termQuery("name", "snow")
            }.must {
              matchQuery("location", "the wall")
            }
          }
        }
      }

      request.show.trim shouldBe
        """{
          |  "query" : {
          |    "bool" : {
          |      "must" : [
          |        {
          |          "match" : {
          |            "location" : {
          |              "query" : "the wall",
          |              "operator" : "OR",
          |              "prefix_length" : 0,
          |              "max_expansions" : 50,
          |              "fuzzy_transpositions" : true,
          |              "lenient" : false,
          |              "zero_terms_query" : "NONE",
          |              "boost" : 1.0
          |            }
          |          }
          |        }
          |      ],
          |      "should" : [
          |        {
          |          "term" : {
          |            "name" : {
          |              "value" : "snow",
          |              "boost" : 1.0
          |            }
          |          }
          |        }
          |      ],
          |      "disable_coord" : false,
          |      "adjust_pure_negative" : true,
          |      "boost" : 1.0
          |    }
          |  },
          |  "ext" : { }
          |}""".stripMargin.trim
    }
  }
}
