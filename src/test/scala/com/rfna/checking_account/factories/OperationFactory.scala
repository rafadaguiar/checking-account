package com.rfna.checking_account.factories

import java.time.LocalDate

import com.rfna.checking_account.models.{Operation, OperationType}
import org.bson.types.ObjectId


object OperationFactory {
  def buildPartialOperation(
    operationType: OperationType,
    amount: BigDecimal,
    date: LocalDate
  ): (ObjectId) => Operation = {
    operationId: ObjectId => Operation(operationId.toString, operationType, amount, date)
  }
}
