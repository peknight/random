ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

ThisBuild / organization := "com.peknight"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-Xfatal-warnings",
    "-language:strictEquality",
    "-Xmax-inlines:64"
  ),
)

lazy val random = (project in file("."))
  .aggregate(
    randomCore.jvm,
    randomCore.js,
  )
  .enablePlugins(JavaAppPackaging)
  .settings(commonSettings)
  .settings(
    name := "random",
  )

lazy val randomCore = (crossProject(JSPlatform, JVMPlatform) in file("random-core"))
  .settings(commonSettings)
  .settings(
    name := "random-core",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core" % catsVersion,
      "org.scalacheck" %%% "scalacheck" % scalaCheckVersion % Test,
      "com.peknight" %%% "generic-core" % pekGenericCoreVersion % Test,
    ),
  )

val catsVersion = "2.9.0"
val scalaCheckVersion = "1.17.0"
val pekGenericCoreVersion = "0.1.0-SNAPSHOT"
