package com.rfna.checking_account.db

import java.util.Date

import com.rfna.checking_account.db.fields.CheckingAccountFields
import com.rfna.checking_account.models.CheckingAccount
import io.circe.optics.JsonPath._
import org.bson.types.ObjectId
import org.mongodb.scala.ScalaObservable
import org.mongodb.scala.bson._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

trait CheckingAccountMongoDAO extends CheckingAccountBaseDAO with MongoDBBaseDAO {
  val ACCOUNTS = root.collections.accounts.string.getOption(conf).get
  private val accountsCollection = db.getCollection(ACCOUNTS)

  override def insertAccount(): Future[CheckingAccount] = {
    val id = new ObjectId()
    val createdAt = new Date()
    val doc = Document(
      CheckingAccountFields.ID -> id,
      CheckingAccountFields.CREATED_AT -> createdAt
    )
    accountsCollection.insertOne(doc)
      .map(_ => CheckingAccount(id.toString, createdAt))
      .head()
  }

  override def findAccount(accountId: String): Future[Option[CheckingAccount]] = {
    val id = Try(new ObjectId(accountId)).toOption
    if (id.isEmpty) return Future(None)

    accountsCollection.find(Document(CheckingAccountFields.ID -> id))
      .map(doc => CheckingAccount.fromDocument(doc))
      .head()
      .map(Option(_))
  }
}
