package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.IndexAndType
import org.elasticsearch.client.Requests
import org.elasticsearch.cluster.routing.Preference
import org.elasticsearch.index.VersionType
import org.elasticsearch.search.fetch.subphase.FetchSourceContext

case class GetDefinition(indexAndType: IndexAndType, id: String) {
  require(indexAndType != null, "indexAndType must not be null")
  require(id.toString.nonEmpty, "id must not be null or empty")

  private val _builder = Requests.getRequest(indexAndType.index).`type`(indexAndType.`type`).id(id)
  def build = _builder

  def fetchSourceContext(context: Boolean) = {
    _builder.fetchSourceContext(new FetchSourceContext(context))
    this
  }

  def fetchSourceContext(include: Iterable[String], exclude: Iterable[String] = Nil) = {
    _builder.fetchSourceContext(new FetchSourceContext(include.toArray, exclude.toArray))
    this
  }

  def fetchSourceContext(context: FetchSourceContext) = {
    _builder.fetchSourceContext(context)
    this
  }

  @deprecated("use storedFields", "5.0.0")
  def fields(fs: String*): GetDefinition = storedFields(fs)

  @deprecated("use storedFields", "5.0.0")
  def fields(fs: Iterable[String]): GetDefinition = storedFields(fs)

  def storedFields(first: String, rest: String*): GetDefinition = storedFields(first +: rest)
  def storedFields(fs: Iterable[String]): GetDefinition = {
    _builder.storedFields(fs.toSeq: _*)
    this
  }

  def parent(p: String) = {
    _builder.parent(p)
    this
  }

  def preference(pref: com.sksamuel.elastic4s.Preference): GetDefinition = preference(pref.value)
  def preference(pref: Preference): GetDefinition = preference(pref.`type`())
  def preference(pref: String): GetDefinition = {
    _builder.preference(pref)
    this
  }

  def realtime(r: Boolean) = {
    _builder.realtime(r)
    this
  }

  def refresh(refresh: Boolean) = {
    _builder.refresh(refresh)
    this
  }

  def routing(r: String) = {
    _builder.routing(r)
    this
  }

  def version(version: Long) = {
    _builder.version(version)
    this
  }

  def versionType(versionType: VersionType) = {
    _builder.versionType(versionType)
    this
  }
}
