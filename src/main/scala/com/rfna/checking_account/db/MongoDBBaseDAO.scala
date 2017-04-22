package com.rfna.checking_account.db

import com.rfna.checking_account.utils.{Configurations, Environment}
import io.circe.optics.JsonPath._
import org.mongodb.scala.MongoClient
import org.slf4j.LoggerFactory

import scala.util.Properties.envOrNone

trait MongoDBBaseDAO {
  protected val conf = Configurations.getConf("/mongo_conf.yaml")
  protected val MONGO_URI = getMongoUri()
  protected val DATABASE_NAME = root.database_name.string.getOption(conf).get
  protected val client = MongoClient(MONGO_URI)
  val db = client.getDatabase(DATABASE_NAME)


  private def getMongoUri(): String = {
    val mongoUri = root.mongo_uri.string.getOption(conf).get
    if (Environment.getEnv() == "production" || Environment.getEnv() == "ci") {
      val user = envOrNone("dbuser").get
      val password = envOrNone("dbpassword").get
      mongoUri.replaceFirst("<dbuser>", user).replaceFirst("<dbpassword>", password)
    } else {
      mongoUri
    }
  }
}
