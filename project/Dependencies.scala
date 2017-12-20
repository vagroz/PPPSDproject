import sbt._

object Dependencies {
  val akkaV = "2.4.19"
  val akkaHttpV = "10.0.9"
  val dispatchV = "0.12.0"


  def akka = Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV
  )

  def configTypesafe = Seq (
    "com.typesafe" % "config" % "1.3.1"
  )



  def slick = Seq(
    "com.typesafe.slick" %% "slick" % "3.2.1",
    "org.xerial" % "sqlite-jdbc" % "3.21.0",
    "org.slf4j" % "slf4j-simple" % "1.7.12"
  )

  def scalaTest = Seq (
    "org.scalactic" %% "scalactic" % "3.0.4",
    "org.scalatest" %% "scalatest" % "3.0.4" % "test"

  )
}
