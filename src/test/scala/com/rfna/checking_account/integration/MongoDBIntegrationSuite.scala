package com.rfna.checking_account.integration

import java.time.LocalDate

import com.rfna.checking_account.core.CheckingAccountInternals
import com.rfna.checking_account.db.{CheckingAccountMongoDAO, OperationsMongoDAO}
import com.rfna.checking_account.models._
import com.rfna.checking_account.utils.MongoUtils._
import org.bson.types.ObjectId
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class MongoDBIntegrationSuite
  extends FunSpec
    with Matchers
    with BeforeAndAfter
    with CheckingAccountMongoDAO
    with OperationsMongoDAO {

  val checkingAccountInternals = new {} with CheckingAccountInternals {}

  val operationIds = Seq.fill(7)(new ObjectId().toString)

  before {
    dropCollection(db, ACCOUNTS)
    dropCollection(db, OPERATIONS)
  }

  val operations = List(
    Operation(operationIds(0), operationType = OperationType.CREDIT, amount = 1000.0, date = LocalDate.of(2017, 4, 1)),
    Operation(operationIds(1), operationType = OperationType.DEBIT, amount = 200.0, date = LocalDate.of(2017, 4, 2)),
    Operation(operationIds(2), operationType = OperationType.WITHDRAWAL, amount = 100.0, date = LocalDate.of(2017, 4, 2)),
    Operation(operationIds(3), operationType = OperationType.PURCHASE, amount = 1000.0, date = LocalDate.of(2017, 4, 4)),
    Operation(operationIds(4), operationType = OperationType.PURCHASE, amount = 200.0, date = LocalDate.of(2017, 4, 4)),
    Operation(operationIds(5), operationType = OperationType.SALARY, amount = 300.0, date = LocalDate.of(2017, 4, 1)),
    Operation(operationIds(6), operationType = OperationType.DEPOSIT, amount = 100.0, date = LocalDate.of(2017, 4, 1))
  )

  describe("When requesting the account balance") {
    it("should be able to get the operations from the database and return the right balance") {
      val account = insertAccount()
      insertOperations(account, operations)
      checkingAccountInternals.getBalance(
        getOperations(account)
      ) shouldBe Balance(-100.0)
    }
  }

  describe("When requesting the statements") {
    it("should be able to get the operations from the database and return the right statements") {
      val account = insertAccount()
      insertOperations(account, operations)
      val (start, end) = (LocalDate.of(2017, 4, 1), LocalDate.of(2017, 4, 3))
      checkingAccountInternals.getStatementsBetween(
        getOperations(account),
        start = start,
        end = end
      ) shouldBe List(
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
    }
  }

  describe("When requesting the periods of debt") {
    it("should be able to get the operations from the database and return the right periods of debt") {
      val account = insertAccount()
      insertOperations(account, operations)
      checkingAccountInternals.getPeriodsOfDebt(
        getOperations(account)
      ) shouldBe List(Debt(LocalDate.of(2017, 4, 4), None, amount = 100))
    }
  }
}
