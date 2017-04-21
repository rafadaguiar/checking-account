package com.rfna.checking_account.controllers

import java.time.LocalDate

import com.rfna.checking_account.core.CheckingAccountInternals
import com.rfna.checking_account.db.{CheckingAccountMongoDAO, OperationsMongoDAO}
import com.rfna.checking_account.models.{CheckingAccount, Operation}
import com.rfna.checking_account.service.encoders_and_decoders.Body._
import fs2.Task
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._

trait CheckingAccountController extends CheckingAccountInternals with CheckingAccountMongoDAO with OperationsMongoDAO {

  def createAccount(): Task[Response] = Created(insertAccount().asJson)

  def getAccount(accountId: String): Task[Response] = {
    doIfAccountIsDefined(accountId, account => account.get.asJson)
  }

  def insertAccountOperations(accountId: String, request: Request) = {
    for {
      operations <- request.as(jsonOf[List[Operation]])
      response <- doIfAccountIsDefined(accountId, account => insertOperations(account.get, operations).asJson)
    } yield (response)
  }

  def getAccountBalance(accountId: String) = {
    doIfAccountIsDefined(accountId, account => getBalance(getOperations(account.get)).asJson)
  }

  def getAccountDebts(accountId: String) = {
    doIfAccountIsDefined(accountId, account => getPeriodsOfDebt(getOperations(account.get)).asJson)
  }

  def getAccountStatements(accountId: String, startDate: LocalDate, endDate: LocalDate) = {
    doIfAccountIsDefined(accountId, account =>
      getStatementsBetween(getOperations(account.get), startDate, endDate).asJson
    )
  }

  private def doIfAccountIsDefined(accountId: String, f: Option[CheckingAccount] => Json): Task[Response] = {
    val account = findAccount(accountId)
    if (account.isDefined) Ok(f(account).asJson) else NotFound(accountId)
  }
}
