package com.rfna.checking_account.models

import java.time.LocalDate

import org.mongodb.scala.bson.ObjectId

sealed trait BaseOperation {
  val operationType: OperationType
  val amount: BigDecimal
  val date: LocalDate

  def value: BigDecimal = operationType.value * amount
}

case class Operation(id: String, operationType: OperationType, amount: BigDecimal, date: LocalDate) extends BaseOperation

case class MongoDBOperation(
  id: ObjectId,
  accountId: ObjectId,
  operationType: OperationType,
  amount: BigDecimal,
  date: LocalDate
) extends BaseOperation {

  def this(account: CheckingAccount, operation: Operation) {
    this(
      new ObjectId(operation.id),
      new ObjectId(account.id),
      operation.operationType,
      operation.amount,
      operation.date
    )
  }
}
