package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.get.{GetResponse, MultiGetRequest, MultiGetRequestBuilder, MultiGetResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.index.VersionType
import org.elasticsearch.search.fetch.subphase.FetchSourceContext

import scala.concurrent.Future

trait GetExecutables {

  implicit object GetDefinitionExecutable extends Executable[GetDefinition, GetResponse, RichGetResponse] {
    override def apply(c: Client, t: GetDefinition): Future[RichGetResponse] = {

      val req = c.prepareGet(t.indexAndType.index, t.indexAndType.`type`, t.id)

      if (t.storedFields.nonEmpty)
        req.setStoredFields(t.storedFields: _*)
      t.realtime.foreach(req.setRealtime)
      t.routing.foreach(req.setRouting)
      t.refresh.foreach(req.setRefresh)
      t.parent.foreach(req.setParent)
      t.fetchSource.foreach { context =>
        if (context.fetchSource) {
          req.setFetchSource(true)
          req.setFetchSource(context.includes, context.excludes)
        } else {
          req.setFetchSource(false)
        }
      }
      t.preference.foreach(req.setPreference)
      t.version.foreach(req.setVersion)
      t.versionType.map(VersionType.fromString).foreach(req.setVersionType)

      injectFutureAndMap(req.execute)(RichGetResponse)
    }
  }

  implicit object MultiGetDefinitionExecutable
    extends Executable[MultiGetDefinition, MultiGetResponse, RichMultiGetResponse] {

    override def apply(c: Client, t: MultiGetDefinition): Future[RichMultiGetResponse] = {
      val _builder = builder(c, t)
      injectFutureAndMap(_builder.execute)(RichMultiGetResponse.apply)
    }

    def builder(c: Client, t: MultiGetDefinition): MultiGetRequestBuilder = {

      val builder = c.prepareMultiGet()

      t.preference.foreach(builder.setPreference)
      t.realtime.foreach(builder.setRealtime)
      t.refresh.foreach(builder.setRefresh)
      t.gets foreach { get =>

        val item = new MultiGetRequest.Item(get.indexAndType.index, get.indexAndType.`type`, get.id)

        get.fetchSource.foreach { context =>
          item.fetchSourceContext(new FetchSourceContext(context.fetchSource, context.includes, context.excludes))
        }

        get.routing.foreach(item.routing)
        get.version.foreach(item.version)
        get.versionType.map(VersionType.fromString).foreach(item.versionType)
        get.parent.foreach(item.parent)

        if (get.storedFields.nonEmpty)
          item.storedFields(get.storedFields: _*)

        builder.add(item)
      }

      builder
    }
  }
}
