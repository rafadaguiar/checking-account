package com.rfna.checking_account.db

import com.rfna.checking_account.utils.MongoUtils._
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class CheckingAccountMongoDAOSuite extends FunSpec with Matchers with BeforeAndAfter with CheckingAccountMongoDAO {

  after {
    dropCollection(db, ACCOUNTS)
  }

  describe("When inserting an account") {
    it("should successfully create an account") {
      insertAccount() should not be null
    }
  }

  describe("When finding an account") {
    it("should find it if it exists") {
      val account = insertAccount()
      findAccount(account.id) shouldBe account
    }
  }
}
