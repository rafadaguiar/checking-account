package com.rfna.checking_account.core

import java.time.LocalDate

import com.rfna.checking_account.models.{Balance, Debt, Operation, OperationType}
import org.bson.types.ObjectId
import org.scalatest.{FunSpec, Matchers}

class CheckingAccountInternalsSuite extends FunSpec with Matchers {
  val checkingAccount = new {} with CheckingAccountInternals {}
  val operationId = "1" * 24
  describe("When getting a balance") {
    it("should return zero if there are no operations") {
      checkingAccount.getBalance(Nil) shouldBe Balance(0)
    }

    it("should return a positive value for a positive sequence") {
      val operations = List(
        Operation(operationId, operationType = OperationType.CREDIT, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.SALARY, amount = 200, date = LocalDate.of(2017, 3, 31)),
        Operation(operationId, operationType = OperationType.DEPOSIT, amount = 300, date = LocalDate.of(2017, 4, 2))
      )
      checkingAccount.getBalance(operations) shouldBe Balance(600)
    }

    it("should get a positive balance in an overly positive sequence") {
      val operations = List(
        Operation(operationId, operationType = OperationType.CREDIT, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.WITHDRAWAL, amount = 100, date = LocalDate.of(2017, 3, 31)),
        Operation(operationId, operationType = OperationType.DEPOSIT, amount = 1, date = LocalDate.of(2017, 4, 2))
      )
      checkingAccount.getBalance(operations) should be > Balance(0)
    }

    it("should get a negative balance in an overly negative sequence") {
      val operations = List(
        Operation(operationId, operationType = OperationType.PURCHASE, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.DEBIT, amount = 100, date = LocalDate.of(2017, 3, 31)),
        Operation(operationId, operationType = OperationType.SALARY, amount = 50, date = LocalDate.of(2017, 4, 2))
      )
      checkingAccount.getBalance(operations) should be < Balance(0)
    }

    it("should compute the exact balance") {
      val operations = List(
        Operation(operationId, operationType = OperationType.PURCHASE, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.DEBIT, amount = 100, date = LocalDate.of(2017, 3, 31)),
        Operation(operationId, operationType = OperationType.SALARY, amount = 50, date = LocalDate.of(2017, 4, 2))
      )
      checkingAccount.getBalance(operations) shouldBe Balance(-150)
    }

    it("should return zero for a single operation with negative type and 0.0 amount") {
      val operations = List(
        Operation(operationId, operationType = OperationType.DEBIT, amount = 0, date = LocalDate.now())
      )
      checkingAccount.getBalance(operations) shouldBe Balance(0)
    }
  }

  describe("When getting statements between") {
    val operations = List(
      Operation(operationId, operationType = OperationType.DEPOSIT, amount = 100, date = LocalDate.of(2017, 5, 2)),
      Operation(operationId, operationType = OperationType.CREDIT, amount = 100, date = LocalDate.of(2017, 5, 2)),
      Operation(operationId, operationType = OperationType.DEBIT, amount = 100, date = LocalDate.of(2017, 5, 3)),
      Operation(operationId, operationType = OperationType.PURCHASE, amount = 100, date = LocalDate.of(2017, 4, 3)),
      Operation(operationId, operationType = OperationType.WITHDRAWAL, amount = 100, date = LocalDate.of(2017, 4, 3)),
      Operation(operationId, operationType = OperationType.SALARY, amount = 100, date = LocalDate.of(2017, 4, 1))
    )

    it("should return the correct number of days") {
      val (start, end) = (LocalDate.of(2017, 4, 1), LocalDate.of(2017, 5, 4))
      checkingAccount.getStatementsBetween(operations, start, end).length shouldBe 4
    }

    it("should return the correct number of operations per day") {
      val (start, end) = (LocalDate.of(2017, 4, 1), LocalDate.of(2017, 5, 4))
      val statements = checkingAccount.getStatementsBetween(operations, start, end)
      statements.map(_.operations.length) shouldBe List(1, 2, 2, 1)
    }

    it("should return the correct balances per day") {
      val (start, end) = (LocalDate.of(2017, 4, 1), LocalDate.of(2017, 5, 4))
      val statements = checkingAccount.getStatementsBetween(operations, start, end)
      statements.map(_.balance) shouldBe List(100, -100, 100, 0)
    }

    it("should return an empty list if the interval doesn't match any records") {
      val (start, end) = (LocalDate.of(2016, 4, 1), LocalDate.of(2016, 5, 4))
      checkingAccount.getStatementsBetween(operations, start, end) shouldBe empty
    }

    it("should return an empty list if start is after the end") {
      val (start, end) = (LocalDate.of(2017, 6, 1), LocalDate.of(2017, 5, 4))
      checkingAccount.getStatementsBetween(operations, start, end) shouldBe empty
    }

    it("should be able to return records if the interval comprises only one day") {
      val (start, end) = (LocalDate.of(2017, 4, 1), LocalDate.of(2017, 4, 2))
      checkingAccount.getStatementsBetween(operations, start, end).length shouldBe 1
    }
  }

  describe("When getting periods of debt") {
    it("should be empty for an empty list of operations") {
      checkingAccount.getPeriodsOfDebt(Nil) shouldBe empty
    }

    it("should be empty if there are no periods of debt") {
      val operations = List(
        Operation(operationId, operationType = OperationType.SALARY, amount = 1000, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.WITHDRAWAL, amount = 200, date = LocalDate.of(2017, 4, 2)),
        Operation(operationId, operationType = OperationType.DEBIT, amount = 100, date = LocalDate.of(2017, 4, 2)),
        Operation(operationId, operationType = OperationType.PURCHASE, amount = 100, date = LocalDate.of(2017, 4, 3)),
        Operation(operationId, operationType = OperationType.PURCHASE, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.PURCHASE, amount = 100, date = LocalDate.of(2017, 4, 1))
      )
      checkingAccount.getPeriodsOfDebt(operations) shouldBe empty
    }

    it("should return a period of [day, None) if presented with a single day that happens to be of debt") {
      val day = LocalDate.now()
      val operations = List(
        Operation(operationId, operationType = OperationType.PURCHASE, amount = 100, date = day)
      )
      checkingAccount.getPeriodsOfDebt(operations) shouldBe List(Debt(day, None, 100))
    }

    it("should return a period of [day, day] if presented with a period of debt of one day") {
      val day = LocalDate.of(2017, 4, 2)
      val operations = List(
        Operation(operationId, operationType = OperationType.PURCHASE, amount = 200, date = day),
        Operation(operationId, operationType = OperationType.DEBIT, amount = 100, date = day),
        Operation(operationId, operationType = OperationType.DEPOSIT, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.DEPOSIT, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.DEPOSIT, amount = 200, date = LocalDate.of(2017, 4, 3))
      )
      checkingAccount.getPeriodsOfDebt(operations) shouldBe List(Debt(day, Some(day), 100))
    }

    it("should return a period of [day, None) if presented with a period of debt that hasn't ended") {
      val day = LocalDate.of(2017, 4, 2)
      val operations = List(
        Operation(operationId, operationType = OperationType.PURCHASE, amount = 100, date = LocalDate.of(2017, 4, 5)),
        Operation(operationId, operationType = OperationType.DEBIT, amount = 100, date = LocalDate.of(2017, 4, 5)),
        Operation(operationId, operationType = OperationType.WITHDRAWAL, amount = 200, date = LocalDate.of(2017, 4, 3)),
        Operation(operationId, operationType = OperationType.PURCHASE, amount = 200, date = day),
        Operation(operationId, operationType = OperationType.DEBIT, amount = 100, date = day),
        Operation(operationId, operationType = OperationType.WITHDRAWAL, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.DEPOSIT, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.DEPOSIT, amount = 100, date = LocalDate.of(2017, 4, 1))
      )
      checkingAccount.getPeriodsOfDebt(operations) shouldBe List(Debt(day, None, 600))
    }

    it("should return multiple periods of debt if present") {
      val operations = List(
        Operation(operationId, operationType = OperationType.WITHDRAWAL, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.DEPOSIT, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.DEPOSIT, amount = 100, date = LocalDate.of(2017, 4, 1)),
        Operation(operationId, operationType = OperationType.PURCHASE, amount = 200, date = LocalDate.of(2017, 4, 2)),
        Operation(operationId, operationType = OperationType.DEBIT, amount = 100, date = LocalDate.of(2017, 4, 2)),
        Operation(operationId, operationType = OperationType.WITHDRAWAL, amount = 200, date = LocalDate.of(2017, 4, 3)),
        Operation(operationId, operationType = OperationType.SALARY, amount = 500, date = LocalDate.of(2017, 4, 4)),
        Operation(operationId, operationType = OperationType.DEBIT, amount = 200, date = LocalDate.of(2017, 4, 5))
      )
      checkingAccount.getPeriodsOfDebt(operations) shouldBe List(
        Debt(LocalDate.of(2017, 4, 2), Some(LocalDate.of(2017, 4, 3)), 400),
        Debt(LocalDate.of(2017, 4, 5), None, 100)
      )
    }
  }
}
