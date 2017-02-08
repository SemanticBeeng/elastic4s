package com.sksamuel.elastic4s.http.search

import cats.syntax.either._
import com.sksamuel.elastic4s.http.{Shards, SourceAsContentBuilder}
import com.sksamuel.elastic4s.{Hit, HitReader}

case class SearchHit(private val _id: String,
                     private val _index: String,
                     private val _type: String,
                     private val _score: Double,
                     private val _source: Map[String, AnyRef],
                     private val _version: Long) extends Hit {

  override def index: String = _index
  override def id: String = _id
  override def `type`: String = _type
  override def version: Long = _version

  override def sourceAsMap: Map[String, AnyRef] = _source
  override def sourceAsString: String = SourceAsContentBuilder(_source).string()

  override def exists: Boolean = true
}

case class SearchHits(total: Int,
                      private val max_score: Double,
                      hits: Array[SearchHit]) {
  def maxScore: Double = max_score
  def size: Int = hits.length
  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty
}

case class SuggestionEntry(term: String) {
  def options: Seq[String] = Nil
  def optionsText: String = ""
}

case class CompletionSuggestionResult(entries: Seq[SuggestionEntry]) {
  def entry(term: String): SuggestionEntry = entries.find(_.term == term).get
}

case class PhraseSuggestionResult(entries: Seq[SuggestionEntry]) {
  def entry(term: String): SuggestionEntry = entries.find(_.term == term).get
}

case class SuggestionOption(text: String, score: Double, freq: Int)

case class SuggestionResult(text: String,
                            offset: Int,
                            length: Int,
                            options: Seq[SuggestionOption]) {
  def toTerm: TermSuggestionResult = TermSuggestionResult(text, offset, length, options)
}

case class TermSuggestionResult(text: String,
                                offset: Int,
                                length: Int,
                                options: Seq[SuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}

case class SearchResponse(took: Int,
                          private val timed_out: Boolean,
                          private val terminated_early: Boolean,
                          private val suggest: Map[String, Seq[SuggestionResult]],
                          _shards: Shards,
                          hits: SearchHits) {

  def totalHits: Int = hits.total
  def size: Int = hits.size
  def ids: Seq[String] = hits.hits.map(_.id)
  def maxScore: Double = hits.maxScore

  def isTimedOut: Boolean = timed_out
  def isTerminatedEarly: Boolean = terminated_early

  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty

  private def suggestion(name: String): Map[String, SuggestionResult] = suggest(name).map { result => result.text -> result }.toMap

  def termSuggestion(name: String): Map[String, TermSuggestionResult] = suggestion(name).mapValues(_.toTerm)

  def completionSuggestion(name: String): CompletionSuggestionResult = suggestion(name).asInstanceOf[CompletionSuggestionResult]
  def phraseSuggestion(name: String): PhraseSuggestionResult = suggestion(name).asInstanceOf[PhraseSuggestionResult]

  def to[T: HitReader]: IndexedSeq[T] = safeTo.flatMap(_.toOption)
  def safeTo[T: HitReader]: IndexedSeq[Either[Throwable, T]] = hits.hits.map(_.safeTo[T]).toIndexedSeq
}


