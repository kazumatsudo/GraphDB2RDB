ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

ThisBuild / javacOptions ++= Seq("-source", "11", "-target", "11")

// FIXME: enable all checks
wartremoverErrors ++= Warts.allBut(
  Wart.Any,
  Wart.AnyVal,
  Wart.AutoUnboxing,
  Wart.FinalCaseClass,
  Wart.GlobalExecutionContext,
  Wart.ImplicitParameter,
  Wart.IterableOps,
  Wart.JavaSerializable,
  Wart.LeakingSealed,
  Wart.NonUnitStatements,
  Wart.Nothing,
  Wart.OptionPartial,
  Wart.Overloading,
  Wart.PlatformDefault,
  Wart.Product,
  Wart.Serializable,
  Wart.StringPlusAny,
  Wart.SeqApply,
  Wart.Throw
)

coverageEnabled := true

lazy val root = (project in file("."))
  .settings(
    name := "GraphDB2RDB"
  )

// main
libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.4.14",
  "com.michaelpollmeier" %% "gremlin-scala" % "3.5.3.7",
  "com.typesafe" % "config" % "1.4.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "io.circe" %% "circe-core" % "0.14.6",
  "io.circe" %% "circe-generic" % "0.14.6",
  "io.circe" %% "circe-parser" % "0.14.6",
  "org.apache.tinkerpop" % "gremlin-driver" % "3.7.1",
  "org.apache.tinkerpop" % "tinkergraph-gremlin" % "3.7.1",
  "org.janusgraph" % "janusgraph-driver" % "1.1.0-20240117-115715.343f146"
)

// test
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)
