package com.sksamuel.elastic4s.json4s

import com.sksamuel.elastic4s.searches.RichSearchHit
import com.sksamuel.elastic4s.{Hit, HitAs, HitReader, Indexable}
import org.json4s._

import scala.reflect.Manifest
import scala.util.control.NonFatal

object ElasticJson4s {
  object Implicits {

    @deprecated("use HitReader which can be used for both get and search APIs", "5.0.0")
    implicit def Json4sHitAs[T](implicit json4s: Serialization,
                                formats: Formats,
                                mf: Manifest[T]): HitAs[T] = new HitAs[T] {
      override def as(hit: RichSearchHit): T = json4s.read[T](hit.sourceAsString)
    }

    implicit def Json4sHitReader[T](implicit json4s: Serialization,
                                    formats: Formats,
                                    mf: Manifest[T]): HitReader[T] = new HitReader[T] {
      override def read(hit: Hit): Either[Throwable, T] = try {
        Right(json4s.read[T](hit.sourceAsString))
      } catch {
        case NonFatal(e) => Left(e)
      }
    }

    implicit def Json4sIndexable[T <: AnyRef](implicit json4s: Serialization,
                                              formats: Formats): Indexable[T] = new Indexable[T] {
      override def json(t: T): String = json4s.write(t)
    }
  }
}
