package com.rfna.checking_account.models

import java.time.LocalDate

case class Statement(date: LocalDate, operations: List[Operation], balance: BigDecimal)