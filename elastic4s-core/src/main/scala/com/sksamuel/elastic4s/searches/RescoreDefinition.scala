package com.sksamuel.elastic4s.searches

import org.elasticsearch.search.rescore.{QueryRescoreMode, RescoreBuilder}

case class RescoreDefinition(query: QueryDefinition) {

  val builder = RescoreBuilder.queryRescorer(query.builder)

  def window(size: Int): RescoreDefinition = {
    builder.windowSize(size)
    this
  }

  def originalQueryWeight(weight: Double): RescoreDefinition = {
    builder.setQueryWeight(weight.toFloat)
    this
  }

  def rescoreQueryWeight(weight: Double): RescoreDefinition = {
    builder.setRescoreQueryWeight(weight.toFloat)
    this
  }

  def scoreMode(mode: String): RescoreDefinition = scoreMode(QueryRescoreMode.valueOf(mode))
  def scoreMode(mode: QueryRescoreMode): RescoreDefinition = {
    builder.setScoreMode(mode)
    this
  }
}
