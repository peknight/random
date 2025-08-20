import com.peknight.build.gav.*
import com.peknight.build.sbt.*

commonSettings

lazy val random = (project in file("."))
  .settings(name := "random")
  .aggregate(
    randomCore.jvm,
    randomCore.js,
    randomMonocle.jvm,
    randomMonocle.js,
  )

lazy val randomCore = (crossProject(JVMPlatform, JSPlatform) in file("random-core"))
  .settings(name := "random-core")
  .settings(crossDependencies(
    typelevel.catsEffect,
    scodec.bits,
  ))
  .settings(crossTestDependencies(
    scalaCheck,
    peknight.generic
  ))

lazy val randomMonocle = (crossProject(JVMPlatform, JSPlatform) in file("random-monocle"))
  .dependsOn(randomCore)
  .settings(name := "random-monocle")
  .settings(crossDependencies(optics.monocle))
