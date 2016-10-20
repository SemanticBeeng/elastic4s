package com.sksamuel.elastic4s.circe

import com.sksamuel.elastic4s.RichSearchHit
import org.elasticsearch.search.SearchHit
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar._
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class CodecDerivationTest extends WordSpec with Matchers with GivenWhenThen {

  case class Place(id: Int, name: String)
  case class Cafe(name: String, place: Place)

  "A derived HitAs instance" should {

    "be implicitly found if circe.generic.auto is in imported" in {
      import io.circe.generic.auto._
      "implicitly[HitAs[Cafe]]" should compile
    }

    "not compile if no decoder is in scope" in {
      "implicitly[HitAs[Cafe]]" shouldNot compile
    }

    "extract the correct values" in {
      import io.circe.generic.auto._
      Given("a search hit")
      val javaHit = mock[SearchHit]
      val hit = RichSearchHit(javaHit)

      val source = """
        { "name": "Cafe Blue", "place": { "id": 3, "name": "Munich" } }
      """

      When("it is parsed with HitAs instances")
      Mockito.when(javaHit.sourceAsString).thenReturn(source)
      val cafe = hit.as[Cafe]

      Then("it contains the correct values")
      cafe.name should be("Cafe Blue")
      cafe.place.id should be(3)
      cafe.place.name should be("Munich")

    }
  }

  "A derived Indexable instance" should {
    "be implicitly found if circe.generic.auto is in imported" in {
      "implicitly[Indexable[Cafe]]" should compile
    }

    "not compile if no decoder is in scope" in {
      "implicitly[Indexable[Cafe]]" shouldNot compile
    }
  }

}
