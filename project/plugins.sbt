// In project/plugins.sbt. Note, does not support sbt 0.13, only sbt 1.x.
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.1.6")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.9")

// https://docs.scala-lang.org/overviews/contributors/index.html#setup-continuous-publication
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.12")