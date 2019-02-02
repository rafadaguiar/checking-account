package com.rfna.checking_account.core

import java.time.LocalDate

import com.rfna.checking_account.models._

import scala.annotation.tailrec

trait CheckingAccountInternals {
  def getBalance(operations: List[Operation]): Balance = {
    Balance(operations.foldLeft(BigDecimal.decimal(0.0))(_ + _.value))
  }

  def getStatementsBetween(operations: List[Operation], start: LocalDate, end: LocalDate)
  : List[Statement] = {
    getStatements(operations)
      .filter { statement =>
        (statement.date.isEqual(start) || statement.date.isAfter(start)) &&
          (statement.date.isEqual(end) || statement.date.isBefore(end))
      }
  }

  def getPeriodsOfDebt(operations: List[Operation]): List[Debt] = {
    val simpleStatements = getStatements(operations)
      .map(statement => SimpleStatement(statement.date, statement.balance))

    @tailrec
    def loop(
      statements: List[SimpleStatement],
      start: Option[SimpleStatement],
      end: Option[SimpleStatement],
      debts: List[Debt]
    ): List[Debt] = statements match {
      case Nil =>
        appendLastDebtIfAny(start, end, debts)
      case statement :: tail if foundADebtStart(statement.balance, start) =>
        loop(tail, Some(statement), Some(statement), debts)
      case statement :: tail if debtContinued(statement.balance, start) =>
        loop(tail, start, Some(statement), debts)
      case statement :: tail if debtCeased(statement.balance, start) =>
        loop(tail, None, None, Debt.fromPeriod(start, end) :: debts)
      case SimpleStatement(_, _) :: tail =>
        loop(tail, start, end, debts)
    }
    loop(simpleStatements, start = None, end = None, debts = Nil).reverse
  }

  private def getStatements(operations: List[Operation]): List[Statement] = {
    @tailrec
    def loop(
      operationsGroupedAndSortedByDate: List[(LocalDate, List[Operation])],
      accBalance: BigDecimal,
      statements: List[Statement]
    ): List[Statement] = operationsGroupedAndSortedByDate match {
      case Nil => statements
      case (date, operations) :: tail =>
        val newBalance = accBalance + getBalance(operations).value
        loop(tail, newBalance, Statement(date, operations, newBalance) :: statements)
    }

    val operationsGroupedAndSortedByDate = getOperationsGroupedAndSortedByDate(operations)
    loop(operationsGroupedAndSortedByDate, accBalance = 0, statements = Nil).reverse
  }

  private def getOperationsGroupedAndSortedByDate(operations: List[Operation])
  : List[(LocalDate, List[Operation])] = {
    val operationsGroupedByDate = groupOperationsByDate(operations)
    operationsGroupedByDate.sortWith { case (op1, op2) => op1._1.isBefore(op2._1) }
  }

  private def groupOperationsByDate(operations: List[Operation])
  : List[(LocalDate, List[Operation])] = {
    operations.groupBy(_.date).toList
  }

  private def appendLastDebtIfAny(
    start: Option[SimpleStatement],
    end: Option[SimpleStatement],
    debts: List[Debt]
  ): List[Debt] = {
    if (start.isDefined) Debt(start.get.date, None, end.get.balance.abs) :: debts else debts
  }

  private def foundADebtStart(balance: BigDecimal, start: Option[SimpleStatement])
  : Boolean = balance < 0 && start.isEmpty

  private def debtContinued(balance: BigDecimal, start: Option[SimpleStatement])
  : Boolean = balance < 0 && start.isDefined

  private def debtCeased(balance: BigDecimal, start: Option[SimpleStatement])
  : Boolean = balance >= 0 && start.isDefined
}
