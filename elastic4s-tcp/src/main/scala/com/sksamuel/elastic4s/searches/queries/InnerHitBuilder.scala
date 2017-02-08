package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.script.SortBuilderFn
import com.sksamuel.elastic4s.searches.HighlightOptionsDefinition
import com.sksamuel.elastic4s.searches.highlighting.HighlightBuilderFn
import org.elasticsearch.index.query.InnerHitBuilder

import scala.collection.JavaConverters._

object InnerHitBuilder {
  def apply(d: InnerHitDefinition): InnerHitBuilder = {
    val builder = new InnerHitBuilder().setName(d.name)
    d.from.foreach(builder.setFrom)
    d.explain.foreach(builder.setExplain)
    d.fetchSource.foreach(builder.setFetchSourceContext)
    d.trackScores.foreach(builder.setTrackScores)
    d.version.foreach(builder.setVersion)
    d.size.foreach(builder.setSize)
    d.docValueFields.foreach(builder.addDocValueField)
    d.sorts.map(SortBuilderFn.apply).foreach(builder.addSort)
    if (d.storedFieldNames.nonEmpty)
      builder.setStoredFieldNames(d.storedFieldNames.asJava)
    if (d.highlights.nonEmpty) {
      val h = HighlightBuilderFn(HighlightOptionsDefinition(), d.highlights)
      builder.setHighlightBuilder(h)
    }
    builder
  }
}
