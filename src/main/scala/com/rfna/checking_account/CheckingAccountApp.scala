package com.rfna.checking_account


import java.util.concurrent.Executors

import com.rfna.checking_account.service.CheckingAccountService
import fs2.{Stream, Task}
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.util.StreamApp

import scala.util.Properties.envOrElse

object CheckingAccountApp extends StreamApp {
  val PORT = envOrElse("PORT", "5000").toInt

  val pool = Executors.newCachedThreadPool()
  override def main(args: List[String]): Stream[Task, Nothing] =
    BlazeBuilder
      .bindHttp(PORT, "0.0.0.0")
      .mountService(CheckingAccountService.service)
      .withServiceExecutor(pool)
      .serve
}