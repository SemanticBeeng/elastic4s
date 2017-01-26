package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.XContentFieldValueWriter
import com.sksamuel.elastic4s.http.{HttpExecutable, RefreshPolicyHttpValue}
import com.sksamuel.elastic4s.indexes.IndexDefinition
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

import scala.collection.JavaConverters._

trait IndexHttpExecutables {

  implicit object IndexHttpExecutable extends HttpExecutable[IndexDefinition, IndexResponse] {

    override def execute(client: RestClient, request: IndexDefinition): (ResponseListener) => Any = {

      val (method, endpoint) = request.id match {
        case Some(id) => "PUT" -> s"/${request.indexAndType.index}/${request.indexAndType.`type`}/$id"
        case None => "POST" -> s"/${request.indexAndType.index}/${request.indexAndType.`type`}"
      }

      val params = scala.collection.mutable.Map.empty[String, String]
      request.opType.foreach(opType => params.put("op_type", opType.lowercase))
      request.routing.foreach(params.put("routing", _))
      request.parent.foreach(params.put("parent", _))
      request.timeout.foreach(params.put("timeout", _))
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))

      val body = IndexContentBuilder(request)
      val entity = new StringEntity(body.string)

      logger.debug(s"Endpoint=$endpoint")
      client.performRequestAsync(method, endpoint, params.asJava, entity, _: ResponseListener)
    }
  }
}

object IndexContentBuilder {
  def apply(request: IndexDefinition): XContentBuilder = {
    request.source match {
      case Some(json) => XContentFactory.jsonBuilder().rawValue(new BytesArray(json))
      case None =>
        val source = XContentFactory.jsonBuilder().startObject()
        request.fields.foreach(XContentFieldValueWriter(source, _))
        source.endObject()
        source
    }
  }
}
