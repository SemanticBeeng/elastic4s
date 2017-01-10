package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.analyzers.WhitespaceAnalyzer
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class IndexExistsTest extends WordSpec with ElasticSugar with Matchers {

  client.execute {
    createIndex("flowers").mappings {
      mapping("flowers") as Seq(
        field("name") withType StringType stored true analyzer WhitespaceAnalyzer,
        field("latin_name") withType StringType
      )
    }
  }.await

  "an index exists request" should {
    "return true for an existing index" in {
      client.execute {
        indexExists("flowers")
      }.await.isExists shouldBe true
    }
    "return false for non existing index" in {
      client.execute {
        indexExists("qweqwewqe")
      }.await.isExists shouldBe false
    }
  }

  "a delete index request" should {
    "delete the index" in {

      client.execute {
        indexExists("flowers")
      }.await.isExists shouldBe true

      deleteIndex("flowers")

      client.execute {
        indexExists("flowers")
      }.await.isExists shouldBe false
    }
  }
}
