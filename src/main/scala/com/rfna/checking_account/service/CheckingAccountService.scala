package com.rfna.checking_account.service

import com.rfna.checking_account.controllers.CheckingAccountController
import com.rfna.checking_account.service.Resources._
import com.rfna.checking_account.service.encoders_and_decoders.Query.{EndDateMatcher, StartDateMatcher}
import org.http4s._
import org.http4s.dsl._

object CheckingAccountService extends CheckingAccountController {
  val service = HttpService {
    case POST -> Root / ACCOUNT / CREATE => createAccount()

    case GET -> Root / ACCOUNT / accountId => getAccount(accountId)

    case request@POST -> Root / ACCOUNT / accountId / OPERATIONS => insertAccountOperations(accountId, request)

    case GET -> Root / ACCOUNT / accountId / BALANCE => getAccountBalance(accountId)

    case GET -> Root / ACCOUNT / accountId / DEBTS => getAccountDebts(accountId)

    case GET -> Root / ACCOUNT / accountId / STATEMENTS :? StartDateMatcher(startDate) +& EndDateMatcher(endDate) =>
      getAccountStatements(accountId, startDate, endDate)
  }
}