package com.rfna.checking_account.models

import java.util.Date

import com.rfna.checking_account.db.fields.CheckingAccountFields
import org.bson.Document

case class CheckingAccount(id: String, createdAt: Date)

object CheckingAccount {
  def fromDocument(accountDocument: Document): CheckingAccount = {
    val createdAt = accountDocument.getDate(CheckingAccountFields.CREATED_AT)
    val id = accountDocument.getObjectId(CheckingAccountFields.ID).toString
    CheckingAccount(id, createdAt)
  }
}
