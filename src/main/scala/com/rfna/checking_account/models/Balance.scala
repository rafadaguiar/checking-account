package com.rfna.checking_account.models

case class Balance(value: Double) extends Ordered[Balance] {
  override def compare(that: Balance) = this.value.compare(that.value)
}
