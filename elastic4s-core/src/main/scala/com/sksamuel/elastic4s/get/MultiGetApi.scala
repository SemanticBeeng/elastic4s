package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.get._
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait MultiGetApi extends GetDsl {

  def multiget(first: GetDefinition, rest: GetDefinition*): MultiGetDefinition = multiget(first +: rest)
  def multiget(gets: Iterable[GetDefinition]): MultiGetDefinition = MultiGetDefinition(gets.toSeq)

  implicit object MultiGetDefinitionExecutable
    extends Executable[MultiGetDefinition, MultiGetResponse, RichMultiGetResponse] {
    override def apply(c: Client, t: MultiGetDefinition): Future[RichMultiGetResponse] = {
      val builder = c.prepareMultiGet()
      t.populate(builder)
      injectFutureAndMap(builder.execute)(RichMultiGetResponse.apply)
    }
  }
}
