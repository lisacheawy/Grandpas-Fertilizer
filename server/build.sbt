name := "game server"

version := "1.0"

scalaVersion := "2.11.8"

val akkaV = "2.5.20"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.4.10",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.10",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.10",
  "com.typesafe.akka" %% "akka-http-testkit" % "2.4.10",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.10",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "org.scalafx" %% "scalafx" % "8.0.92-R10",
)
