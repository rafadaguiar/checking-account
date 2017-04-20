package com.rfna.checking_account.db

import com.rfna.checking_account.models.{CheckingAccount, Operation}

trait OperationsBaseDAO {
  def getOperations(account: CheckingAccount): List[Operation]

  def insertOperations(account: CheckingAccount, operations: List[Operation]): List[String]
}
