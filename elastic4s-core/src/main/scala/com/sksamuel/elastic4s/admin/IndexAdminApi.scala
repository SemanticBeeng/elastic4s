package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.Indexes

trait IndexAdminApi {

  def refreshIndex(first: String, rest: String*): RefreshIndexDefinition = refreshIndex(first +: rest)
  def refreshIndex(indexes: Iterable[String]): RefreshIndexDefinition = refreshIndex(Indexes(indexes))
  def refreshIndex(indexes: Indexes): RefreshIndexDefinition = RefreshIndexDefinition(indexes.values)

  def indexStats(indexes: Indexes): IndicesStatsDefinition = IndicesStatsDefinition(indexes)
  def indexStats(first: String, rest: String*): IndicesStatsDefinition = indexStats(first +: rest)

  def typesExist(types: String*): TypesExistExpectsIn = typesExist(types)
  def typesExist(types: Iterable[String]): TypesExistExpectsIn = new TypesExistExpectsIn(types)
  class TypesExistExpectsIn(types: Iterable[String]) {
    def in(indexes: String*): TypesExistsDefinition = TypesExistsDefinition(indexes, types.toSeq)
  }

  def closeIndex(index: String): CloseIndexDefinition = CloseIndexDefinition(index)
  def openIndex(index: String): OpenIndexDefinition = OpenIndexDefinition(index)

  def getSegments(indexes: Indexes): GetSegmentsDefinition = GetSegmentsDefinition(indexes)
  def getSegments(first: String, rest: String*): GetSegmentsDefinition = getSegments(first +: rest)

  def flushIndex(indexes: Iterable[String]): FlushIndexDefinition = FlushIndexDefinition(indexes.toSeq)
  def flushIndex(indexes: String*): FlushIndexDefinition = flushIndex(indexes)

  def indexExists(indexes: Iterable[String]): IndexExistsDefinition = IndexExistsDefinition(indexes.toSeq)
  def indexExists(indexes: String*): IndexExistsDefinition = IndexExistsDefinition(indexes)

  def clearCache(first: String, rest: String*): ClearCacheDefinition = clearCache(first +: rest)
  def clearCache(indexes: Iterable[String]): ClearCacheDefinition = ClearCacheDefinition(indexes.toSeq)

  def clearIndex(first: String, rest: String*): ClearCacheDefinition = clearIndex(first +: rest)
  def clearIndex(indexes: Iterable[String]): ClearCacheDefinition = ClearCacheDefinition(indexes.toSeq)

  def rollover(alias: String): RolloverDefinition = RolloverDefinition(alias)

  def shrink(source: String, target: String): ShrinkDefinition = ShrinkDefinition(source, target)
}
