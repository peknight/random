ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.1"

ThisBuild / organization := "com.peknight"

ThisBuild / publishTo := {
  val nexus = "https://nexus.peknight.com/repository"
  if (isSnapshot.value)
    Some("snapshot" at s"$nexus/maven-snapshots/")
  else
    Some("releases" at s"$nexus/maven-releases/")
}

ThisBuild / credentials ++= Seq(
  Credentials(Path.userHome / ".sbt" / ".credentials")
)

ThisBuild / resolvers ++= Seq(
  "Pek Nexus" at "https://nexus.peknight.com/repository/maven-public/",
)

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
      "org.typelevel" %%% "cats-effect" % catsEffectVersion,
      "org.scodec" %%% "scodec-bits" % scodecVersion,
      "org.scalacheck" %%% "scalacheck" % scalaCheckVersion % Test,
      "com.peknight" %%% "generic-core" % pekGenericVersion % Test,
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

val catsEffectVersion = "3.6.1"
val scodecVersion = "1.2.1"
val monocleVersion = "3.3.0"
val scalaCheckVersion = "1.18.1"
val pekVersion = "0.1.0-SNAPSHOT"
val pekGenericVersion = pekVersion
