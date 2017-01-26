package com.sksamuel.elastic4s.searches.queries.span

import org.elasticsearch.index.query.{QueryBuilders, SpanTermQueryBuilder}

object SpanTermQueryBuilder {
  def apply(q: SpanTermQueryDefinition): SpanTermQueryBuilder = {
    val builder = q.value match {
      case d: Double => QueryBuilders.spanTermQuery(q.field, d)
      case f: Float => QueryBuilders.spanTermQuery(q.field, f)
      case i: Int => QueryBuilders.spanTermQuery(q.field, i)
      case l: Long => QueryBuilders.spanTermQuery(q.field, l)
      case s: String => QueryBuilders.spanTermQuery(q.field, s)
    }
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    builder
  }
}
