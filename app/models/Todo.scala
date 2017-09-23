package models

import javax.inject.Inject

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import Redis.RedisRequester

import scala.concurrent.{ExecutionContext, Future}

/**
  * Was created by Riccardo Sirigu on 10/08/2017.
  *
  *
  */

case class Todo(_id: Option[BSONObjectID], title: String, completed: Option[Boolean])

object JsonFormats {

  import play.api.libs.json._

  implicit val todoFormat: OFormat[Todo] = Json.format[Todo]
}

object TodoCacheRepository {
  val CACHE_KEY = "todo:latest"
  val ID_CACHE_KEY = "todo:latestID"
}

class TodoCacheRepository @Inject()(implicit ec: ExecutionContext, redisRequester: RedisRequester) {

  import JsonFormats._
  import TodoCacheRepository._

  @deprecated("because the latest doc might changed without adding more docs")
  def setLatest(todo: Todo): Future[Boolean] = redisRequester.set(CACHE_KEY, Json.toJson(todo))

  @deprecated("because the latest doc might changed without adding more docs")
  def getLatest: Future[Option[Todo]] = redisRequester.getJson(CACHE_KEY).map {
    _.map(_.validate[Todo].get)
  }

  def setLatest(bSONObjectID: BSONObjectID): Future[Boolean] = redisRequester.set(ID_CACHE_KEY, Json.toJson(bSONObjectID))

  def getLatestBSONID: Future[Option[BSONObjectID]] = redisRequester.getJson(ID_CACHE_KEY).map {
    _.map(_.validate[BSONObjectID].get)
  }
}

class TodoRepository @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi, cache: TodoCacheRepository) {

  import JsonFormats._

  def todosCollection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("todos"))

  def getAll: Future[Seq[Todo]] = {
    val query = Json.obj()
    todosCollection.flatMap(_.find(query)
      .cursor[Todo](ReadPreference.primary)
      .collect[Seq]()
    )
  }

  def getTodo(id: BSONObjectID): Future[Option[Todo]] = {
    val query = BSONDocument("_id" -> id)
    todosCollection.flatMap(_.find(query).one[Todo])
  }

  def getTodo(futureId: Future[BSONObjectID]): Future[Option[Todo]] = {
    futureId.flatMap {
      id =>
        val query = BSONDocument("_id" -> id)
        todosCollection.flatMap(_.find(query).one[Todo])
    }
  }

  def addTodo(todo: Todo): (BSONObjectID, Future[WriteResult]) = {

    /**
      * Control the generated ID for futher implements
      */
    val id = {
      todo._id match {
        case Some(_id) => _id
        case None => BSONObjectID.generate()
      }
    }

    val todoItem = {
      todo.completed match {
        case Some(_) => todo
        case None => Todo(Some(id), todo.title, completed = Some(false))
      }
    }

    val writeResult = todosCollection.flatMap(_.insert(todoItem))

    writeResult.onComplete {
      _ =>
        cache.setLatest(todoItem)
        cache.setLatest(id)
    }

    id -> writeResult
  }

  def getLatest: Future[Option[Todo]] = getTodo (
    cache.getLatestBSONID.map(todo => todo.getOrElse(BSONObjectID.generate()))
  )


  def updateTodo(id: BSONObjectID, todo: Todo): Future[Option[Todo]] = {

    val selector = BSONDocument("_id" -> id)
    val updateModifier = BSONDocument(
      "$set" -> BSONDocument(
        "title" -> todo.title,
        "completed" -> todo.completed)
    )

    todosCollection.flatMap(
      _.findAndUpdate(selector, updateModifier, fetchNewObject = true).map(_.result[Todo])
    )
  }

  def deleteTodo(id: BSONObjectID): Future[Option[Todo]] = {
    val selector = BSONDocument("_id" -> id)
    todosCollection.flatMap(_.findAndRemove(selector).map(_.result[Todo]))
  }

}