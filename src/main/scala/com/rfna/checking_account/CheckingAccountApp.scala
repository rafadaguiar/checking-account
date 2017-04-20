package com.rfna.checking_account


import java.util.concurrent.Executors

import com.rfna.checking_account.service.CheckingAccountService
import com.rfna.checking_account.utils.Configurations
import fs2.{Stream, Task}
import io.circe.optics.JsonPath._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.util.StreamApp


object CheckingAccountApp extends StreamApp {
  val conf = Configurations.getConf("/webserver_conf.yaml")
  val port = root.port.int.getOption(conf).get
  val ip = root.ip.string.getOption(conf).get

  val pool = Executors.newCachedThreadPool()

  override def main(args: List[String]): Stream[Task, Nothing] =
    BlazeBuilder
      .bindHttp(port, ip)
      .mountService(CheckingAccountService.service)
      .withServiceExecutor(pool)
      .serve
}