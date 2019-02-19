lazy val root = (project in file("."))
  .settings(
    name := "checking_account",
    organization := "com.rfna",
    scalaVersion := "2.12.8",
    version := "0.1.0-SNAPSHOT"
  )
  .enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % "0.17.0-M1",
  "org.http4s" %% "http4s-circe" % "0.17.0-M1",
  "org.http4s" %% "http4s-dsl" % "0.17.0-M1",
  "io.circe" %% "circe-generic" % "0.7.1",
  "io.circe" %% "circe-literal" % "0.7.1",
  "io.circe" %% "circe-optics" % "0.7.1",
  "io.circe" %% "circe-yaml" % "0.5.0",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.0.0",
  "io.netty" % "netty-all" % "4.1.9.Final",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.1"
)