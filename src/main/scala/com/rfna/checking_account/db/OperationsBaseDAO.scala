package com.rfna.checking_account.db

import com.rfna.checking_account.models.{CheckingAccount, Operation}

import scala.concurrent.Future

trait OperationsBaseDAO {
  def getOperations(account: CheckingAccount): Future[List[Operation]]

  def insertOperations(account: CheckingAccount, operations: List[Operation]): Future[List[String]]
}
