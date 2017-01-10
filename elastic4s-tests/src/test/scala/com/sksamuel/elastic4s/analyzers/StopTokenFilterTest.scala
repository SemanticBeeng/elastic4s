package com.sksamuel.elastic4s.analyzers

import org.scalatest.{Matchers, WordSpec}

class StopTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "StopTokenFilter builder" should {
    "set stop words" in {
      stopTokenFilter("testy").stopwords("boo", "foo").json.string shouldBe """{"type":"stop","stopwords":["boo","foo"]}"""
    }
    "set ignore case" in {
      stopTokenFilter("testy").ignoreCase(true).json.string shouldBe """{"type":"stop","ignore_case":true}"""
    }
    "set remove trailing" in {
      stopTokenFilter("testy").removeTrailing(true).json.string shouldBe """{"type":"stop","remove_trailing":true}"""
    }
  }
}
