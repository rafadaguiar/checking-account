package com.rfna.checking_account.db

import java.time.LocalDate
import java.util.Date

import com.rfna.checking_account.models.{CheckingAccount, Operation, OperationType}
import com.rfna.checking_account.utils.MongoUtils._
import org.bson.types.ObjectId
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class OperationsMongoDAOSuite extends FunSpec with Matchers with BeforeAndAfter with OperationsMongoDAO {
  before {
    dropCollection(db, OPERATIONS)
  }

  val account = new CheckingAccount("1" * 24, new Date)

  val operationIds = List(
    new ObjectId().toString,
    new ObjectId().toString,
    new ObjectId().toString,
    new ObjectId().toString,
    new ObjectId().toString,
    new ObjectId().toString,
    new ObjectId().toString
  )
  val operations = List(
    Operation(operationIds(0), OperationType.CREDIT, amount = 1000.0, date = LocalDate.of(2017, 4, 1)),
    Operation(operationIds(1), OperationType.DEBIT, amount = 200.0, date = LocalDate.of(2017, 4, 2)),
    Operation(operationIds(2), OperationType.WITHDRAWAL, amount = 100.0, date = LocalDate.of(2017, 4, 2)),
    Operation(operationIds(3), OperationType.PURCHASE, amount = 100.0, date = LocalDate.of(2017, 4, 4)),
    Operation(operationIds(4), OperationType.PURCHASE, amount = 200.0, date = LocalDate.of(2017, 4, 4)),
    Operation(operationIds(5), OperationType.SALARY, amount = 300.0, date = LocalDate.of(2017, 4, 1)),
    Operation(operationIds(6), OperationType.DEPOSIT, amount = 100.0, date = LocalDate.of(2017, 4, 1))
  )

  describe("When performing writes") {
    it("should successfully insert operations") {
      insertOperations(account, operations) should contain theSameElementsAs operationIds
    }
  }

  describe("When performing reads") {
    it("should successfully get operations") {
      insertOperations(account, operations)
      getOperations(account)
    }
  }
}
