package com.rfna.checking_account.utils

import org.mongodb.scala.{Completed, MongoDatabase}
import com.rfna.checking_account.db.utils.MongoUtils._

object MongoUtils {
  def dropCollection(db: MongoDatabase, collectionName: String): Boolean = {
    db.getCollection(collectionName)
      .drop()
      .results()
      .forall(_ == Completed())
  }
}
