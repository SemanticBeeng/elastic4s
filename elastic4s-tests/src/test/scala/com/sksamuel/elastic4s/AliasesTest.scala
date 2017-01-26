package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConversions._
import com.sksamuel.elastic4s.testkit.{ElasticSugar, SharedElasticSugar}
import org.scalatest.mockito.MockitoSugar

class AliasesTest extends FlatSpec with MockitoSugar with SharedElasticSugar with Matchers {

  client.execute(
    bulk(
      index into "waterways/rivers" id 11 fields("name" -> "River Lune", "country" -> "England"),
      index into "waterways/rivers" id 12 fields("name" -> "River Dee", "country" -> "England"),
      index into "waterways/rivers" id 21 fields("name" -> "River Dee", "country" -> "Wales"),
      index into "waterways_updated/rivers" id 31 fields("name" -> "Thames", "country" -> "England")
    )
  ).await

  refresh("waterways")
  blockUntilCount(3, "waterways")
  blockUntilCount(1, "waterways_updated")

  client.execute {
    add alias "aquatic_locations" on "waterways"
  }.await

  client.execute {
    addAlias("english_waterways").on("waterways").filter(termQuery("country", "england"))
  }.await

  client.execute {
    add alias "moving_alias" on "waterways"
  }.await

  "waterways index" should "return 'River Dee' in England and Wales for search" in {
    val resp = client.execute {
      search in "waterways" query "Dee"
    }.await
    resp.totalHits shouldBe 2
    resp.ids shouldBe Seq("12", "21")
  }

  "aquatic_locations" should "alias waterways" in {
    val resp1 = client.execute {
      get("21").from("aquatic_locations")
    }.await
    resp1.id shouldBe "21"
  }

  it should "alias waterways and accept a type" in {
    val resp2 = client.execute {
      get(21).from("aquatic_locations/rivers")
    }.await
    resp2.id shouldBe "21"
  }

  "english_waterways" should "be an alias with a filter for country=england" in {
    val resp = client.execute {
      search("english_waterways").query("dee")
    }.await
    // 'english_waterways' has a filter for England only, so we only expect to find one River dee
    resp.totalHits shouldBe 1
    resp.hits.head.id shouldBe "12"
  }

  it should "be in query for alias" in {
    val resp = client.execute {
      get alias "english_waterways"
    }.await

    compareAliasesForIndex(resp, "waterways", Set("english_waterways"))
  }

  it should "be in query for alias on waterways" in {
    val resp = client.execute {
      get alias "english_waterways" on "waterways"
    }.await

    compareAliasesForIndex(resp, "waterways", Set("english_waterways"))
  }

  "moving_alias" should "move from 'waterways' to 'waterways_updated'" in {
    val resp = client.execute {
      get alias "moving_alias" on("waterways", "waterways_updated")
    }.await

    compareAliasesForIndex(resp, "waterways", Set("moving_alias"))
    assert(!resp.getAliases.containsKey("waterways_updated"))

    client.execute {
      aliases(
        remove alias "moving_alias" on "waterways",
        add alias "moving_alias" on "waterways_updated"
      )
    }.await

    val respAfterMovingAlias = client.execute {
      getAlias("moving_alias").on("waterways", "waterways_updated")
    }.await

    compareAliasesForIndex(respAfterMovingAlias, "waterways_updated", Set("moving_alias"))
    assert(!respAfterMovingAlias.getAliases.containsKey("waterways"))
  }

  "get alias" should "have implicit conversion to rich response" in {

    val resp = client.execute {
      get alias "english_waterways" on "waterways"
    }.await

    resp.aliases("waterways").head.alias shouldBe "english_waterways"
  }

  private def compareAliasesForIndex(resp: GetAliasesResponse, index: String,
                                     expectedAliases: Set[String]) = {
    val aliases = resp.getAliases.get(index)
    assert(aliases !== null)
    assert(expectedAliases === aliases.map(_.alias()).toSet)
  }
}
