ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

ThisBuild / javacOptions ++= Seq("-source", "11", "-target", "11")

// FIXME: enable all checks
wartremoverErrors ++= Warts.allBut(
  Wart.Any,
  Wart.AutoUnboxing,
  Wart.FinalCaseClass,
  Wart.IterableOps,
  Wart.JavaSerializable,
  Wart.LeakingSealed,
  Wart.NonUnitStatements,
  Wart.Nothing,
  Wart.Overloading,
  Wart.Product,
  Wart.Serializable,
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
  "ch.qos.logback" % "logback-classic" % "1.4.14",
  "com.michaelpollmeier" %% "gremlin-scala" % "3.5.3.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "org.apache.tinkerpop" % "gremlin-driver" % "3.7.1",
  "org.apache.tinkerpop" % "tinkergraph-gremlin" % "3.6.2",
  "org.janusgraph" % "janusgraph-driver" % "1.0.0"
)

// test
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)
