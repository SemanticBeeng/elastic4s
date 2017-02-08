package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.analyzers.AnalyzerDefinition
import com.sksamuel.elastic4s.mappings.MappingDefinition
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration._

case class CreateIndexDefinition(name: String,
                                 analysis: Option[AnalysisDefinition] = None,
                                 mappings: Seq[MappingDefinition] = Nil,
                                 rawSource: Option[String] = None,
                                 waitForActiveShards: Option[Int] = None,
                                 settings: IndexSettings = new IndexSettings) {

  require(!name.contains("/"), "Index should not contain / when creating mappings. Specify the type as the mapping")

  def singleShard(): CreateIndexDefinition = shards(1)
  def singleReplica(): CreateIndexDefinition = replicas(1)

  def waitForActiveShards(shards: Int): CreateIndexDefinition = copy(waitForActiveShards = shards.some)

  def shards(shds: Int): CreateIndexDefinition = copy(settings = settings.shards = shds)
  def replicas(repls: Int): CreateIndexDefinition = copy(settings = settings.replicas = repls)

  def refreshInterval(duration: Duration): CreateIndexDefinition = refreshInterval(duration.toMillis + "ms")
  def refreshInterval(interval: String): CreateIndexDefinition = copy(settings = settings.refreshInterval = interval)

  def indexSetting(name: String, value: Any): CreateIndexDefinition = copy(settings = settings.add(name, value))

  def mappings(first: MappingDefinition, rest: MappingDefinition*): CreateIndexDefinition = mappings(first +: rest)
  def mappings(mappings: Iterable[MappingDefinition]): CreateIndexDefinition = copy(mappings = this.mappings ++ mappings)

  def analysis(first: AnalyzerDefinition, rest: AnalyzerDefinition*): CreateIndexDefinition = analysis(first +: rest)
  def analysis(analyzers: Iterable[AnalyzerDefinition]): CreateIndexDefinition = copy(analysis = AnalysisDefinition(analyzers).some)

  def source(source: String): CreateIndexDefinition = copy(rawSource = source.some)
}
