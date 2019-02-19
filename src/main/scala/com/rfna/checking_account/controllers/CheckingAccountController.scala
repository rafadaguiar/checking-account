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

import scala.concurrent.ExecutionContext.Implicits.global


trait CheckingAccountController extends
  CheckingAccountInternals with CheckingAccountMongoDAO with OperationsMongoDAO {
  implicit val TASK_STRATEGY = fs2.Strategy.fromCachedDaemonPool()

  def createAccount(): Task[Response] = {
    Task.fromFuture(insertAccount())
      .flatMap(acc => Created(acc.asJson))
  }

  def getAccount(accountId: String): Task[Response] = {
    doIfAccountIsDefined(accountId)(account => Ok(account.asJson))
  }

  def insertAccountOperations(accountId: String, request: Request): Task[Response] = {
    for {
      operations <- request.as(jsonOf[List[Operation]])
      response <- doIfAccountIsDefined(accountId) { account =>
        Created(insertOperations(account, operations).map(_.asJson))
      }
    } yield (response)
  }

  def getAccountBalance(accountId: String): Task[Response] = {
    doIfAccountIsDefined(accountId) { account =>
      Ok(getOperations(account).map(ops => getBalance(ops).asJson))
    }
  }

  def getAccountDebts(accountId: String): Task[Response] = {
    doIfAccountIsDefined(accountId) { account =>
      Ok(getOperations(account).map(ops => getPeriodsOfDebt(ops).asJson))
    }
  }

  def getAccountStatements(accountId: String, startDate: LocalDate, endDate: LocalDate)
  : Task[Response] = {
    doIfAccountIsDefined(accountId) { account =>
      Ok(getOperations(account).map(ops => getStatementsBetween(ops, startDate, endDate).asJson))
    }
  }

  private def doIfAccountIsDefined(accountId: String)(f: CheckingAccount => Task[Response])
  : Task[Response] = {
    Task.fromFuture(findAccount(accountId))
      .flatMap(acc => if (acc.isDefined) f(acc.get) else NotFound(accountId))
  }
}
