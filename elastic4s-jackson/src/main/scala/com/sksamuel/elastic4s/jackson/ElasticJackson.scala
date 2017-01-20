package com.sksamuel.elastic4s.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.sksamuel.elastic4s._
import com.sksamuel.exts.Logging

import scala.util.control.NonFatal

object ElasticJackson {

  object Implicits extends Logging {

    implicit val mapper = JacksonSupport.mapper

    implicit def format[T: Manifest](implicit mapper: ObjectMapper): JsonFormat[T] = new JsonFormat[T] {
      override def toJson(t: T): String = mapper.writeValueAsString(t)
      override def fromJson(json: String): T = {
        val t = manifest.runtimeClass.asInstanceOf[Class[T]]
        logger.debug(s"Deserializing $json to $t")
        mapper.readValue[T](json, t)
      }
    }

    implicit def JacksonJsonIndexable[T](implicit mapper: ObjectMapper): Indexable[T] = new Indexable[T] {
      override def json(t: T): String = mapper.writeValueAsString(t)
    }

    implicit def JacksonJsonHitReader[T: Manifest](implicit mapper: ObjectMapper): HitReader[T] = new HitReader[T] {
      override def read(hit: Hit): Either[Throwable, T] = {
        try {
          val node = mapper.readTree(hit.sourceAsString).asInstanceOf[ObjectNode]
          if (!node.has("_id")) node.put("_id", hit.id)
          if (!node.has("_type")) node.put("_type", hit.`type`)
          if (!node.has("_index")) node.put("_index", hit.index)
          //  if (!node.has("_score")) node.put("_score", hit.score)
          if (!node.has("_version")) node.put("_version", hit.version)
          if (!node.has("_timestamp")) hit.sourceFieldOpt("_timestamp").collect {
            case f => f.toString
          }.foreach(node.put("_timestamp", _))
          Right(mapper.readValue[T](mapper.writeValueAsBytes(node), manifest.runtimeClass.asInstanceOf[Class[T]]))
        } catch {
          case NonFatal(e) => Left(e)
        }
      }
    }
  }
}

