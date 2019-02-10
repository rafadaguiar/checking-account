package com.rfna.checking_account.db

import com.rfna.checking_account.models.CheckingAccount

import scala.concurrent.Future

trait CheckingAccountBaseDAO {
  def insertAccount(): Future[CheckingAccount]

  def findAccount(accountId: String): Future[Option[CheckingAccount]]
}
