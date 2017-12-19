import sbt._

object Dependencies {
  val akkaV = "2.4.19"
  val akkaHttpV = "10.0.9"
  val dispatchV = "0.12.0"


  def akka = Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-actor" % akkaV
  )

  def configTypesafe = Seq (
    "com.typesafe" % "config" % "1.3.1"
  )


  def slick = Seq(
    "com.typesafe.slick" %% "slick" % "3.2.1",
    "org.xerial" % "sqlite-jdbc" % "3.7.15-M1"

  )
}
