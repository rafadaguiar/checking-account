package com.rfna.checking_account.db

import java.util.Date

import com.rfna.checking_account.db.fields.CheckingAccountFields
import com.rfna.checking_account.db.utils.MongoUtils._
import com.rfna.checking_account.models.CheckingAccount
import io.circe.optics.JsonPath._
import org.bson.types.ObjectId
import org.mongodb.scala.bson._

import scala.util.Try

trait CheckingAccountMongoDAO extends CheckingAccountBaseDAO with MongoDBBaseDAO {
  val ACCOUNTS = root.collections.accounts.string.getOption(conf).get
  private val accountsCollection = db.getCollection(ACCOUNTS)

  override def insertAccount(): CheckingAccount = {
    val id = new ObjectId
    val createdAt = new Date()
    accountsCollection.insertOne(
      Document(
        CheckingAccountFields.ID -> id,
        CheckingAccountFields.CREATED_AT -> createdAt
      )
    ).results()
    CheckingAccount(id.toString, createdAt)
  }

  override def findAccount(accountId: String): Option[CheckingAccount] = {
    val id = Try(new ObjectId(accountId)).toOption
    if (id.isDefined) findAccountOrNone(id.get) else None
  }

  private def findAccountOrNone(id: ObjectId): Option[CheckingAccount] = {
    val accountDocument = accountsCollection.find(
      Document(CheckingAccountFields.ID -> id)
    ).results().headOption

    if (accountDocument.isDefined) Some(CheckingAccount.fromDocument(accountDocument.get)) else None
  }
}
