package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.testkit.ElasticSugar
import com.sksamuel.elastic4s.{RefreshPolicy, VersionType}
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FlatSpec, Matchers}

class DeleteApiTest extends FlatSpec with Matchers with ElasticSugar with TypeCheckedTripleEquals {

  "a delete by id request" should "accept tuple for from" in {
    delete id 141212 from "places" -> "cities"
  }

  it should "parse slash indextype" in {
    delete id 141212 from "index/type"
  }

  it should "accept index and type in dot syntax" in {
    delete(123).from("places", "type1")
    delete(123).from("places", "type1")
  }

  it should "accept tuple in dot syntax" in {
    delete(123).from("places" -> "type1")
    delete(123).from("places" -> "type1")
  }

  it should "accept routing key" in {
    delete(141212).from("places" / "type1").routing("my-route")
  }

  it should "accept version and version type" in {
    val req = delete id 141212 from "places" / "type1" version 53423l versionType VersionType.External
  }

  it should "accept refresh" in {
    delete(141212) from "places" / "type1" refresh RefreshPolicy.Immediate
  }

  "a delete by query request" should "support the dsl syntax" in {
    deleteIn("places").by("query")
  }
}
