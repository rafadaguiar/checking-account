package com.rfna.checking_account.db

import com.rfna.checking_account.models.{CheckingAccount, Operation}

trait CheckingAccountBaseDAO {
  def insertAccount(): CheckingAccount

  def findAccount(accountId: String): Option[CheckingAccount]
}
