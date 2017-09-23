package Redis

import javax.inject.Inject

import akka.actor.ActorSystem
import org.slf4j.LoggerFactory
import play.api.libs.json.JsValue
import redis.{RedisClientPool, RedisServer}

import scala.concurrent.{ExecutionContext, Future}

object RedisRequester {
  val logger = LoggerFactory.getLogger("services.database.RedisRequester")

  logger.info("REDIS CLIENT POOL INIT")
}

class RequesterServer {
  implicit val akkaSystem: ActorSystem = ActorSystem()

  val server: RedisServer = RedisServer()
  val redisPool = RedisClientPool(Seq(server))
}

class RedisRequester @Inject() (val requesterServer: RequesterServer) (implicit executionContext: ExecutionContext) {


  import Formaters._
  import RedisRequester._

  def redisPool: RedisClientPool = requesterServer.redisPool

  def get(key: String): Future[Option[String]] = {
    logger.info(s"REDIS GET key=$key.")
    redisPool.get[String](key)
  }

  def append(key: String, value: String): Future[Long] = {
    redisPool.append[String](key, value)
  }

  def set(key: String, value: String): Future[Boolean] = {
    logger.info(s"REDIS SET key=$key value=$value.")
    redisPool.set[String](key, value)
  }

  def delete(key: String): Future[Long] = {
    logger.info(s"REDIS DELETE key=$key.")
    redisPool.del(key)
  }

  def contains(key: String): Future[Boolean] =
    redisPool.exists(key)

  def replace(key: String, value: String): Future[Boolean] =
    this.contains(key).flatMap {
      flag: Boolean =>
        if (flag) {
          logger.info(s"FOUND EXIST key=$key. BEGIN REPLACE BY SET NEW VALUE.")
          this.set(key, value)
        } else Future {
          logger.warn(s"Key=$key NOT FOUND. OPERATOR CANCELLED.")
          false
        }
    }

  def getJson(key: String): Future[Option[JsValue]] = {
    logger.info(s"REDIS GET JSON key=$key")
    redisPool.get[JsValue](key)
  }

  def set(key: String, json: JsValue) = {
    logger.info(s"REDIS SET key=$key with jsonValue=${json.toString()}")
    redisPool.set[JsValue](key, json)
  }

  def replace(key: String, json: JsValue): Future[Boolean] =
    this.contains(key).flatMap {
      flag: Boolean =>
        if (flag) {
          logger.info(s"FOUND EXIST JSON key=$key. BEGIN REPLACE BY SET NEW VALUE.")
          this.set(key, json)
        } else Future {
          logger.warn(s"Key=$key NOT FOUND. OPERATOR CANCELLED.")
          false
        }
    }
}