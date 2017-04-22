package com.rfna.checking_account.models

import java.time.LocalDate

case class Debt(start: LocalDate, end: Option[LocalDate], amount: BigDecimal)

object Debt {
  def fromPeriod(start: Option[SimpleStatement], end: Option[SimpleStatement]): Debt = {
    new Debt(start.get.date, Some(end.get.date), end.get.balance.abs)
  }
}
