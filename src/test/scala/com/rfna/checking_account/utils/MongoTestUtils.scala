package com.rfna.checking_account.utils

import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object MongoTestUtils {
  val futureTimeout = 10 seconds
  val ID = "_id"

  def cleanInsertions[T](db: MongoDatabase, collectionName: String, insertions: List[T])
                        (getObjectId: T => ObjectId): Boolean = {
    Await.result(db.getCollection(collectionName)
      .deleteMany(in(ID, insertions.map(getObjectId): _*))
      .toFuture().map(_.wasAcknowledged), 10 seconds)
  }
}
