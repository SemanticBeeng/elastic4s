package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{FlatSpec, Matchers}

class GetDslTest extends FlatSpec with Matchers with ElasticSugar {

  "a get by id request" should "accept tuple for from" in {
    val req = get id 123 from "places" -> "cities"
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "accept two parameters" in {
    val req = get id 123 from("places", "cities")
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "parse slash indextype" in {
    val req = get id 123 from "places/cities"
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "accept one field" in {
    val req = get id 123 from "places/cities" fields "name"
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
    req.build.storedFields() shouldBe Array("name")
  }

  it should "accept multiple fields" in {
    val req = get id 123 from "places/cities" fields("name", "title", "content")
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
    req.build.storedFields() shouldBe Array("name", "title", "content")
  }

  it should "disable fetchSource" in {
    val req = get(123).from("places/cities").fetchSourceContext(false)
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
    req.build.fetchSourceContext().fetchSource shouldBe false
  }

  it should "should support routing" in {
    val req = get(123).from("places" / "cities").routing("aroundwego")
    assert(req.build.routing() === "aroundwego")
  }

  it should "should support parent" in {
    val req = get id 123 from "places/cities" parent "whosyour"
    assert(req.build.parent() === "whosyour")
  }
}
