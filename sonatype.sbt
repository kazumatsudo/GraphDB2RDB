// https://docs.scala-lang.org/overviews/contributors/index.html#setup-your-project
// used as `artifactId`
name := "GraphDB2RDB"

// used as `groupId`
organization := "io.github.kazumatsudo"

// open source licenses that apply to the project
licenses := Seq(
  "APL2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")
)

description := "generate RDB DDL/DML from GraphDB"

import xerial.sbt.Sonatype.*
sonatypeProjectHosting := Some(
  GitHubHosting("kazumatsudo", "GraphDB2RDB", "graphdb2rdb@edandit.co.jp")
)

// publish to the sonatype repository
publishTo := sonatypePublishToBundle.value
