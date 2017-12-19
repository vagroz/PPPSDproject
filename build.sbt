import Dependencies._

name := "PPPSDproject"

val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.12.4",
  libraryDependencies ++= configTypesafe,
  libraryDependencies ++= scalaTest
)

lazy val core = project
  .settings(commonSettings)

lazy val pppsdproject = (project in file("."))
  .settings(commonSettings)
  .settings(mainClass in assembly := Some("pppsdproject.Main"))
  .settings(assemblyJarName in assembly := "pppsdproject.jar")
  .dependsOn(core, dbservice, webserver)
  .aggregate(core, dbservice, webserver)

lazy val dbservice = project
  .settings(commonSettings)
  .dependsOn(core)


lazy val webserver = project
  .settings(commonSettings)
  .dependsOn(core)
  .dependsOn(dbservice)
  .settings(libraryDependencies ++= akka)



