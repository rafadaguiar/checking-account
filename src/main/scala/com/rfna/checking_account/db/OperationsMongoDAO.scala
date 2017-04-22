package com.rfna.checking_account.db

import java.time.ZoneId
import java.util.Date

import com.rfna.checking_account.db.fields.OperationFields
import com.rfna.checking_account.db.utils.MongoUtils._
import com.rfna.checking_account.models.{CheckingAccount, Operation, OperationType}
import io.circe.optics.JsonPath._
import org.mongodb.scala.bson.{BsonDecimal128, Document, ObjectId}
import org.mongodb.scala.model.Filters._

trait OperationsMongoDAO extends OperationsBaseDAO with MongoDBBaseDAO {
  val OPERATIONS = root.collections.operations.string.getOption(conf).get
  private val operationsCollection = db.getCollection(OPERATIONS)
  private val UTC_ZONE_ID = ZoneId.of("UTC")

  override def insertOperations(account: CheckingAccount, operations: List[Operation]): List[String] = {
    val (ids, documents) = operations.map(operation => toMongoDocument(account, operation)).unzip
    operationsCollection.insertMany(documents).results()
    ids.map(_.toString)
  }

  private def toMongoDocument(account: CheckingAccount, operation: Operation): (ObjectId, Document) = {
    val utcDate = Date.from(operation.date.atStartOfDay(UTC_ZONE_ID).toInstant)
    val id = new ObjectId(operation.id)
    (id, Document(
      OperationFields.ID -> id,
      OperationFields.ACCOUNT_ID -> new ObjectId(account.id),
      OperationFields.DESCRIPTION -> operation.operationType.description,
      OperationFields.AMOUNT -> operation.amount,
      OperationFields.DATE -> utcDate
    ))
  }

  override def getOperations(account: CheckingAccount): List[Operation] = {
    operationsCollection
      .find(equal(OperationFields.ACCOUNT_ID, new ObjectId(account.id)))
      .results()
      .map(fromMongoDocument)
      .toList
  }

  private def fromMongoDocument(document: Document): Operation = {
    val id = document.getObjectId(OperationFields.ID).toString
    val description = document.getString(OperationFields.DESCRIPTION)
    val operationType = OperationType.withName(description)
    val amount = document.get[BsonDecimal128](OperationFields.AMOUNT).get.getValue.bigDecimalValue()
    val date = document.getDate(OperationFields.DATE).toInstant.atZone(UTC_ZONE_ID).toLocalDate
    Operation(id, operationType = operationType, amount = amount, date = date)
  }
}
