package com.rfna.checking_account.db

import com.rfna.checking_account.utils.Configurations
import io.circe.optics.JsonPath._
import org.mongodb.scala.MongoClient

trait MongoDBBaseDAO {
  protected val conf = Configurations.getConf("/mongo_conf.yaml")
  protected val MONGO_URI = root.mongo_uri.string.getOption(conf).get
  protected val DATABASE_NAME = root.database_name.string.getOption(conf).get
  protected val client = MongoClient(MONGO_URI)
  val db = client.getDatabase(DATABASE_NAME)
}
