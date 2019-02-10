package com.rfna.checking_account.db

import com.rfna.checking_account.utils.MongoTestUtils._
import org.bson.types.ObjectId
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.concurrent.Await

class CheckingAccountMongoDAOSuite extends FunSpec with Matchers with BeforeAndAfter with CheckingAccountMongoDAO {

  describe("When inserting an account") {
    it("should successfully create an account") {
      val account = Await.result(insertAccount(), futureTimeout)
      account should not be null
      cleanInsertions(db, collectionName = ACCOUNTS, insertions = List(account))(acc => new
          ObjectId(acc.id))
    }
  }

  describe("When finding an account") {
    it("should find it if exists") {
      val account = Await.result(insertAccount(), futureTimeout)
      Await.result(findAccount(account.id), futureTimeout).get shouldBe account
      cleanInsertions(db, collectionName = ACCOUNTS, insertions = List(account))(acc => new ObjectId(acc.id))
    }

    it("shouldn't raise exceptions when the account id is invalid") {
      val invalidAccountId = "1"
      noException shouldBe thrownBy(Await.result(findAccount(invalidAccountId), futureTimeout))
    }

    it("should return None when the account doesn't exists") {
      val unregisteredAccountId = "1" * 24
      Await.result(findAccount(unregisteredAccountId), futureTimeout) shouldBe None
    }
  }
}
