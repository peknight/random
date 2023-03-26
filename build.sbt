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
    randomGeneric.jvm,
    randomGeneric.js,
    randomEffect.jvm,
    randomEffect.js,
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
      "com.peknight" %%% "generic-core" % pekGenericVersion % Test,
    ),
  )

lazy val randomGeneric = (crossProject(JSPlatform, JVMPlatform) in file("random-generic"))
  .dependsOn(randomCore)
  .settings(commonSettings)
  .settings(
    name := "random-generic",
    libraryDependencies ++= Seq(
      "com.peknight" %%% "generic-mapper" % pekGenericVersion,
    ),
  )

lazy val randomEffect = (crossProject(JSPlatform, JVMPlatform) in file("random-effect"))
  .dependsOn(randomCore)
  .settings(commonSettings)
  .settings(
    name := "random-effect",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-effect" % catsEffectVersion,
    ),
  )

val catsVersion = "2.9.0"
val catsEffectVersion = "3.4.6"
val scalaCheckVersion = "1.17.0"
val pekGenericVersion = "0.1.0-SNAPSHOT"
