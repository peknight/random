ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

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
    randomEffect.jvm,
    randomEffect.js,
    randomMonocle.jvm,
    randomMonocle.js,
  )
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

lazy val randomEffect = (crossProject(JSPlatform, JVMPlatform) in file("random-effect"))
  .dependsOn(randomCore)
  .settings(commonSettings)
  .settings(
    name := "random-effect",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-effect" % catsEffectVersion,
    ),
  )

lazy val randomMonocle = (crossProject(JSPlatform, JVMPlatform) in file("random-monocle"))
  .dependsOn(randomCore)
  .settings(commonSettings)
  .settings(
    name := "random-monocle",
    libraryDependencies ++= Seq(
      "dev.optics" %%% "monocle-core" % monocleVersion,
    ),
  )

val catsVersion = "2.10.0"
val catsEffectVersion = "3.5.1"
val monocleVersion = "3.2.0"
val scalaCheckVersion = "1.17.0"
val pekGenericVersion = "0.1.0-SNAPSHOT"
