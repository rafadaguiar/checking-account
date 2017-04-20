package com.rfna.checking_account.controllers

import java.time.LocalDate

import com.rfna.checking_account.core.CheckingAccountInternals
import com.rfna.checking_account.db.{CheckingAccountMongoDAO, OperationsMongoDAO}
import com.rfna.checking_account.models.Operation
import com.rfna.checking_account.service.encoders_and_decoders.Body._
import fs2.Task
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._

trait CheckingAccountController extends CheckingAccountInternals with CheckingAccountMongoDAO with OperationsMongoDAO {

  def createAccount(): Task[Response] = Created(insertAccount().asJson)

  def getAccount(accountId: String): Task[Response] = {
    val account = findAccount(accountId)
    Ok(account.asJson)
  }

  def insertAccountOperations(accountId: String, request: Request) = {
    val account = findAccount(accountId)
    for {
      operations <- request.as(jsonOf[List[Operation]])
      response <- Ok(insertOperations(account, operations).asJson)
    } yield (response)
  }

  def getAccountBalance(accountId: String) = {
    val account = findAccount(accountId)
    Ok(getBalance(getOperations(account)).asJson)
  }

  def getAccountDebts(accountId: String) = {
    val account = findAccount(accountId)
    Ok(getPeriodsOfDebt(getOperations(account)).asJson)
  }

  def getAccountStatements(accountId: String, startDate: LocalDate, endDate: LocalDate) = {
    val account = findAccount(accountId)
    Ok(getStatementsBetween(getOperations(account), startDate, endDate).asJson)
  }
}
