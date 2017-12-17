import Dependencies._

name := "PPPSDproject"

val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.12.4"

)


lazy val pppsdproject = (project in file("."))
  .settings(commonSettings)


lazy val webserver = project
  .settings(commonSettings)
  .dependsOn(core)
  .settings(libraryDependencies ++= akka)

lazy val dbservice = project
  .settings(commonSettings)
  .dependsOn(core)

lazy val core = project
  .settings(commonSettings)