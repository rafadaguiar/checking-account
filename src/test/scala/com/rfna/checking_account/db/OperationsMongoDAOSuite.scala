package com.rfna.checking_account.db

import java.time.LocalDate
import java.util.Date

import com.rfna.checking_account.factories.OperationFactory
import com.rfna.checking_account.models.{CheckingAccount, OperationType}
import com.rfna.checking_account.utils.MongoTestUtils._
import org.bson.types.ObjectId
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

import scala.concurrent.Await

class OperationsMongoDAOSuite extends FunSpec with Matchers with BeforeAndAfterEach with OperationsMongoDAO {
  val account = new CheckingAccount("2" * 24, new Date)

  val partialOperations = List(
    OperationFactory.buildPartialOperation(OperationType.CREDIT, amount = 1000.0, date = LocalDate.of(2017, 4, 1)),
    OperationFactory.buildPartialOperation(OperationType.DEBIT, amount = 200.0, date = LocalDate.of(2017, 4, 2)),
    OperationFactory.buildPartialOperation(OperationType.WITHDRAWAL, amount = 100.0, date = LocalDate.of(2017, 4, 2)),
    OperationFactory.buildPartialOperation(OperationType.PURCHASE, amount = 100.0, date = LocalDate.of(2017, 4, 4)),
    OperationFactory.buildPartialOperation(OperationType.PURCHASE, amount = 200.0, date = LocalDate.of(2017, 4, 4)),
    OperationFactory.buildPartialOperation(OperationType.SALARY, amount = 300.0, date = LocalDate.of(2017, 4, 1)),
    OperationFactory.buildPartialOperation(OperationType.DEPOSIT, amount = 100.0, date = LocalDate.of(2017, 4, 1))
  )

  describe("When performing writes") {
    it("should successfully insert operations") {
      val operations = partialOperations.map(operationWithoutId => operationWithoutId(new ObjectId))
      val operationIds = operations.map(_.id)
      Await.result(
        insertOperations(account, operations), futureTimeout
      ) should contain theSameElementsAs operationIds
      cleanInsertions(db, collectionName = OPERATIONS, insertions = operations)(op => new ObjectId(op.id))
    }
  }

  describe("When performing reads") {
    it("should successfully get operations") {
      val operations = partialOperations.map(operationWithoutId => operationWithoutId(new ObjectId))
      Await.result(insertOperations(account, operations), futureTimeout)
      Await.result(
        getOperations(account), futureTimeout) should contain theSameElementsAs operations
      cleanInsertions(db, collectionName = OPERATIONS, insertions = operations)(op => new ObjectId(op.id))
    }
  }
}
