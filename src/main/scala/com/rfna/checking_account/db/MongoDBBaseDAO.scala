package com.rfna.checking_account.db

import com.rfna.checking_account.utils.{Configurations, Environment}
import io.circe.optics.JsonPath._
import org.mongodb.scala.MongoClient

import scala.util.Properties.envOrNone

trait MongoDBBaseDAO {
  System.setProperty("org.mongodb.async.type", "netty")
  protected val conf = Configurations.getConf("/mongo_conf.yaml")
  protected val MONGO_URI = getMongoUri()
  protected val DATABASE_NAME = root.database_name.string.getOption(conf).get
  protected val client = MongoClient(MONGO_URI)
  val db = client.getDatabase(DATABASE_NAME)


  private def getMongoUri(): String = {
    val mongoUri = root.mongo_uri.string.getOption(conf).get
    if (Environment.getEnv() == "production" || Environment.getEnv() == "ci") {
      val password = envOrNone("PASSWORD").get
      mongoUri.replaceFirst("<PASSWORD>", password)
    } else {
      mongoUri
    }
  }
}
