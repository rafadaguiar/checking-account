package com.rfna.checking_account.utils

import io.circe.Json
import io.circe.yaml.parser

import scala.io.Source

object Configurations {
  def getConf(confPath: String): Json = {
    val source = Source.fromURL(getClass.getResource(confPath))
    val json = parser.parse(source.bufferedReader()).right.get
    json.\\(Environment.getEnv()).head
  }
}
