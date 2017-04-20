package com.rfna.checking_account.models

sealed trait OperationType {
  val description: String
  val value: Double
}

sealed trait AdditiveOperation extends OperationType {
  override val value: Double = +1.0
}

sealed trait SubtractiveOperation extends OperationType {
  override val value: Double = -1.0
}

object OperationType {

  case object CREDIT extends AdditiveOperation {
    override val description = "credit"
  }

  case object DEPOSIT extends AdditiveOperation {
    override val description: String = "deposit"
  }

  case object SALARY extends AdditiveOperation {
    override val description: String = "salary"
  }

  case object DEBIT extends SubtractiveOperation {
    override val description: String = "debit"
  }

  case object PURCHASE extends SubtractiveOperation {
    override val description: String = "purchase"
  }

  case object WITHDRAWAL extends SubtractiveOperation {
    override val description: String = "withdrawal"
  }

  def withName(name: String): OperationType = name match {
    case "credit" => CREDIT
    case "deposit" => DEPOSIT
    case "salary" => SALARY
    case "debit" => DEBIT
    case "purchase" => PURCHASE
    case "withdrawal" => WITHDRAWAL
  }
}


