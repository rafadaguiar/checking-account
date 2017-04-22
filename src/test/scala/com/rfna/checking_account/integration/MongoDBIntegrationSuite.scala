package com.rfna.checking_account.integration

import java.time.LocalDate

import com.rfna.checking_account.core.CheckingAccountInternals
import com.rfna.checking_account.db.{CheckingAccountMongoDAO, OperationsMongoDAO}
import com.rfna.checking_account.factories.OperationFactory
import com.rfna.checking_account.models._
import com.rfna.checking_account.utils.MongoUtils._
import org.bson.types.ObjectId
import org.scalatest.{FunSpec, Matchers}

class MongoDBIntegrationSuite
  extends FunSpec
    with Matchers
    with CheckingAccountMongoDAO
    with OperationsMongoDAO
    with CheckingAccountInternals {

  val partialOperations = List(
    OperationFactory.buildPartialOperation(operationType = OperationType.CREDIT, amount = 1000.0, date = LocalDate.of(2017, 4, 1)),
    OperationFactory.buildPartialOperation(operationType = OperationType.DEBIT, amount = 200.0, date = LocalDate.of(2017, 4, 2)),
    OperationFactory.buildPartialOperation(operationType = OperationType.WITHDRAWAL, amount = 100.0, date = LocalDate.of(2017, 4, 2)),
    OperationFactory.buildPartialOperation(operationType = OperationType.PURCHASE, amount = 1000.0, date = LocalDate.of(2017, 4, 4)),
    OperationFactory.buildPartialOperation(operationType = OperationType.PURCHASE, amount = 200.0, date = LocalDate.of(2017, 4, 4)),
    OperationFactory.buildPartialOperation(operationType = OperationType.SALARY, amount = 300.0, date = LocalDate.of(2017, 4, 1)),
    OperationFactory.buildPartialOperation(operationType = OperationType.DEPOSIT, amount = 100.0, date = LocalDate.of(2017, 4, 1))
  )

  describe("When requesting the account balance") {
    it("should be able to get the operations from the database and return the right balance") {
      val account = insertAccount()
      val operations = partialOperations.map(operationWithoutId => operationWithoutId(new ObjectId))
      insertOperations(account, operations)
      getBalance(getOperations(account)) shouldBe Balance(-100.0)
      cleanCollections(account, operations)
    }
  }

  describe("When requesting the statements") {
    it("should be able to get the operations from the database and return the right statements") {
      val account = insertAccount()
      val operations = partialOperations.map(operationWithoutId => operationWithoutId(new ObjectId))
      val operationIds = operations.map(_.id)
      insertOperations(account, operations)
      val (start, end) = (LocalDate.of(2017, 4, 1), LocalDate.of(2017, 4, 3))
      getStatementsBetween(getOperations(account), start = start, end = end) shouldBe
        List(
          Statement(
            start,
            List(
              Operation(operationIds(0), operationType = OperationType.CREDIT, amount = 1000.0, date = start),
              Operation(operationIds(5), operationType = OperationType.SALARY, amount = 300.0, date = start),
              Operation(operationIds(6), operationType = OperationType.DEPOSIT, amount = 100.0, date = start)
            ),
            balance = 1400.0
          ),
          Statement(
            end.minusDays(1),
            List(
              Operation(operationIds(1), operationType = OperationType.DEBIT, amount = 200.0, date = end.minusDays(1)),
              Operation(operationIds(2), operationType = OperationType.WITHDRAWAL, amount = 100.0, date = end.minusDays(1))
            ),
            balance = 1100.0
          )
        )
      cleanCollections(account, operations)
    }
  }

  describe("When requesting the periods of debt") {
    it("should be able to get the operations from the database and return the right periods of debt") {
      val account = insertAccount()
      val operations = partialOperations.map(operationWithoutId => operationWithoutId(new ObjectId))
      insertOperations(account, operations)
      getPeriodsOfDebt(getOperations(account)) shouldBe List(Debt(LocalDate.of(2017, 4, 4), None, amount = 100))
      cleanCollections(account, operations)
    }
  }

  def cleanCollections(account: CheckingAccount, operations: List[Operation]) = {
    cleanInsertions(db, collectionName = ACCOUNTS, insertions = List(account))(acc => new ObjectId(acc.id))
    cleanInsertions(db, collectionName = OPERATIONS, insertions = operations.map(_.id))(new ObjectId(_))
  }
}
