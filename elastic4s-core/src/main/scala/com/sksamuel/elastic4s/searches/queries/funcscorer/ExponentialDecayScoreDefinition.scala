package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.MultiValueMode

case class ExponentialDecayScoreDefinition(field: String,
                                           origin: String,
                                           scale: String,
                                           offset: Option[Any] = None,
                                           decay: Option[Double] = None,
                                           weight: Option[Double] = None,
                                           multiValueMode: Option[MultiValueMode] = None)
  extends ScoreFunctionDefinition {

  def decay(decay: Double): ExponentialDecayScoreDefinition = copy(decay = decay.some)
  def offset(offset: Any): ExponentialDecayScoreDefinition = copy(offset = offset.some)
}
