ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

ThisBuild / javacOptions ++= Seq("-source", "11", "-target", "11")

lazy val root = (project in file("."))
  .settings(
    name := "GraphDB2RDB"
  )

// main
libraryDependencies ++= Seq(
  "com.michaelpollmeier" %% "gremlin-scala" % "3.5.3.7",
  "org.apache.tinkerpop" % "gremlin-driver" % "3.6.2",
  "org.apache.tinkerpop" % "tinkergraph-gremlin" % "3.6.2",
  "org.janusgraph" % "janusgraph-driver" % "1.0.0"
)

// test
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)
