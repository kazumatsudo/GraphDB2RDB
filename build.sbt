ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

ThisBuild / javacOptions ++= Seq("-source", "11", "-target", "11")

// FIXME: enable all checks
wartremoverErrors ++= Warts.allBut(
  Wart.Any,
  Wart.AnyVal,
  Wart.Equals,
  Wart.ImplicitParameter,
  Wart.NonUnitStatements,
  Wart.Nothing,
  Wart.OptionPartial,
  Wart.PlatformDefault,
  Wart.Recursion,
  Wart.StringPlusAny,
  Wart.Throw
)

coverageEnabled := true

lazy val root = (project in file("."))
  .settings(
    name := "GraphDB2RDB"
  )

// main
libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.5.3",
  "com.michaelpollmeier" %% "gremlin-scala" % "3.5.3.7",
  "com.typesafe" % "config" % "1.4.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "io.circe" %% "circe-core" % "0.14.6",
  "io.circe" %% "circe-generic" % "0.14.6",
  "io.circe" %% "circe-parser" % "0.14.6",
  "org.apache.tinkerpop" % "gremlin-driver" % "3.7.1",
  "org.apache.tinkerpop" % "tinkergraph-gremlin" % "3.7.1",
  "org.janusgraph" % "janusgraph-driver" % "1.1.0-20240228-212357.0310ba4"
)

// test
libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "2.2.224" % Test,
  "com.mysql" % "mysql-connector-j" % "8.3.0" % Test,
  "com.typesafe.slick" %% "slick" % "3.4.1" % Test,
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1" % Test,
  "io.github.etspaceman" %% "scalacheck-faker" % "8.0.2" % Test,
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)
