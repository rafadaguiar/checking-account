package com.rfna.checking_account.db

import com.rfna.checking_account.utils.MongoUtils._
import org.bson.types.ObjectId
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class CheckingAccountMongoDAOSuite extends FunSpec with Matchers with BeforeAndAfter with CheckingAccountMongoDAO {
  describe("When inserting an account") {
    it("should successfully create an account") {
      val account = insertAccount()
      account should not be null
      cleanInsertions(db, collectionName = ACCOUNTS, insertions = List(account))(acc => new ObjectId(acc.id))
    }
  }

  describe("When finding an account") {
    it("should find it if it exists") {
      val account = insertAccount()
      findAccount(account.id) shouldBe Some(account)
      cleanInsertions(db, collectionName = ACCOUNTS, insertions = List(account))(acc => new ObjectId(acc.id))
    }

    it("shouldn't raise exceptions when the account doesn't exists") {
      val invalidAccountId = "1" * 24
      noException shouldBe thrownBy(findAccount(invalidAccountId))
    }

    it("should return None when the account doesn't exists") {
      val invalidAccountId = "1" * 24
      findAccount(invalidAccountId) shouldBe None
    }
  }
}
