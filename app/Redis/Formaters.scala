package Redis

import akka.util.ByteString
import play.api.libs.json.{JsValue, Json}
import redis.ByteStringFormatter

object Formaters {
  implicit val byteStringFormatter = new ByteStringFormatter[String] {
    override def deserialize(bs: ByteString) = bs.utf8String

    override def serialize(data: String) = ByteString(data)
  }

  implicit val jsonByteStringFormatter = new ByteStringFormatter[JsValue] {
    override def deserialize(bs: ByteString): JsValue = Json.parse(bs.utf8String)

    override def serialize(data: JsValue): ByteString = ByteString(data.toString())
  }
}
