name := "game client"

version := "1.0"

scalaVersion := "2.12.8"

val akkaVersion = "2.5.20"
val akkaHttpVersion = "10.1.7"
val scalaTestVersion = "3.0.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
//  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.32",
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
//  "com.typesafe.akka" %% "akka-http-core" % "10.1.15",
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,
  "org.scalafx" %% "scalafx" % "19.0.0-R30",
)
