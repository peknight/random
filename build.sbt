import com.peknight.build.gav.*
import com.peknight.build.sbt.*

commonSettings

ThisBuild / scalacOptions --= Seq("-Werror", "-Xfatal-warnings")

lazy val random = (project in file("."))
  .settings(name := "random")
  .aggregate(randomCore.projectRefs *)
  .aggregate(randomMonocle.projectRefs *)

lazy val randomCore = (projectMatrix in file("random-core"))
  .settings(name := "random-core")
  .settings(libraryDependencies ++= dependencies(
    typelevel.catsEffect,
    scodec.bits,
  ))
  .settings(libraryDependencies ++= testDependencies(
    scalaCheck,
    peknight.generic,
  ))
  .jvmPlatform(scalaVersions = Seq(scala.scala3.version))
  .jsPlatform(scalaVersions = Seq(scala.scala3.version))

lazy val randomMonocle = (projectMatrix in file("random-monocle"))
  .dependsOn(randomCore)
  .settings(name := "random-monocle")
  .settings(libraryDependencies ++= dependencies(optics.monocle))
  .jvmPlatform(scalaVersions = Seq(scala.scala3.version))
  .jsPlatform(scalaVersions = Seq(scala.scala3.version))
