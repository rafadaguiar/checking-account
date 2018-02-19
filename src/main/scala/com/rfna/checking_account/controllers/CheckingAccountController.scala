package com.rfna.checking_account.controllers

import java.time.LocalDate

import com.rfna.checking_account.core.CheckingAccountInternals
import com.rfna.checking_account.db.{CheckingAccountMongoDAO, OperationsMongoDAO}
import com.rfna.checking_account.models.{CheckingAccount, Operation}
import com.rfna.checking_account.service.encoders_and_decoders.Body._
import fs2.Task
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._

trait CheckingAccountController extends
  CheckingAccountInternals with CheckingAccountMongoDAO with OperationsMongoDAO {

  def createAccount(): Task[Response] = Created(insertAccount().asJson)

  def getAccount(accountId: String): Task[Response] = {
    doIfAccountIsDefined(accountId)(account => Ok(account.get.asJson))
  }

  def insertAccountOperations(accountId: String, request: Request): Task[Response] = {
    for {
      operations <- request.as(jsonOf[List[Operation]])
      response <- doIfAccountIsDefined(accountId) { account =>
        Created(insertOperations(account.get, operations).asJson)
      }
    } yield (response)
  }

  def getAccountBalance(accountId: String) = {
    doIfAccountIsDefined(accountId) { account =>
      Ok(getBalance(getOperations(account.get)).asJson)
    }
  }

  def getAccountDebts(accountId: String): Task[Response] = {
    doIfAccountIsDefined(accountId) { account =>
      Ok(getPeriodsOfDebt(getOperations(account.get)).asJson)
    }
  }

  def getAccountStatements(accountId: String, startDate: LocalDate, endDate: LocalDate)
  :Task[Response] = {
    doIfAccountIsDefined(accountId) { account =>
      Ok(getStatementsBetween(getOperations(account.get), startDate, endDate).asJson)
    }
  }

  private def doIfAccountIsDefined(accountId: String)(f: Option[CheckingAccount] => Task[Response])
  : Task[Response] = {
    val account = findAccount(accountId)
    if (account.isDefined) f(account) else NotFound(accountId)
  }
}
