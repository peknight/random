import com.peknight.build.gav.*
import com.peknight.build.sbt.*

commonSettings

lazy val random = (project in file("."))
  .aggregate(
    randomCore.jvm,
    randomCore.js,
    randomMonocle.jvm,
    randomMonocle.js,
  )
  .settings(
    name := "random",
  )

lazy val randomCore = (crossProject(JVMPlatform, JSPlatform) in file("random-core"))
  .settings(crossDependencies(
    typelevel.catsEffect,
    scodec.bits,
  ))
  .settings(crossDependency(scalaCheck, Some(Test)))
  .settings(crossDependency(peknight.generic, Some(Test)))
  .settings(
    name := "random-core",
  )

lazy val randomMonocle = (crossProject(JVMPlatform, JSPlatform) in file("random-monocle"))
  .dependsOn(randomCore)
  .settings(crossDependencies(optics.monocle))
  .settings(
    name := "random-monocle",
  )
