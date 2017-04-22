package com.rfna.checking_account.utils

import com.rfna.checking_account.db.utils.MongoUtils._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{Completed, MongoDatabase}

object MongoUtils {
  val ID = "_id"

  def cleanInsertions[T](
    db: MongoDatabase,
    collectionName: String,
    insertions: List[T]
  )(getObjectId: T => ObjectId): Boolean = {
    db.getCollection(collectionName)
      .deleteMany(in(ID, insertions.map(getObjectId)))
      .results().forall(_ == Completed())
  }
}
