package com.rfna.checking_account.utils

import scala.util.Properties.envOrElse

object Environment {
  val ENVIRONMENT = "CHECKING_ACCOUNT_ENV"
  val DEFAULT = "development"

  def getEnv(): String = envOrElse(ENVIRONMENT, DEFAULT)
}
