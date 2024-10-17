lazy val scala212 = "2.12.11"
lazy val scala211 = "2.11.12"
lazy val supportedScalaVersions = List(scala212, scala211)

val sparkVersion = "3.5.0"
val sparkTestVersion = "3.5.0"

val dependencies = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion,
  "org.apache.spark" %% "spark-avro" % sparkVersion,
  "io.delta" %% "delta-spark" % "3.2.0",
  "com.typesafe" % "config" % "1.4.3",
)

val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.2.19",
  "org.scalamock" %% "scalamock" % "6.0.0",
  "org.apache.hadoop" % "hadoop-minicluster" % "3.3.6",
  "com.holdenkarau" %% "spark-testing-base" % s"${sparkTestVersion}_2.0.1",
  "org.mockito" % "mockito-core" % "4.6.1" % Test
)

import xerial.sbt.Sonatype._

val settings = Seq(
  organization := "com.damavis",
  version := "0.4.0-SNAPSHOT",
  isSnapshot := version.value.endsWith("SNAPSHOT"),
  scalaVersion := "2.12.11",
  libraryDependencies ++= dependencies ++ testDependencies,
  fork in Test := true,
  parallelExecution in Test := false,
  envVars in Test := Map("MASTER" -> "local[*]"),
  test in assembly := {},
  // Sonatype
  sonatypeProfileName := "com.damavis",
  sonatypeProjectHosting := Some(
    GitHubHosting("damavis", "damavis-spark", "info@damavis.com")),
  publishMavenStyle := true,
  licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  developers := List(
    Developer(
      id = "piffall",
      name = "Cristòfol Torrens",
      email = "piffall@gmail.com",
      url = url("http://piffall.com")),
    Developer(
      id = "priera",
      name = "Pedro Riera",
      email = "pedro.riera at damavis dot com",
      url = url("http://github.com/priera"))),
  publishTo := sonatypePublishToBundle.value,
  credentials += Publish.credentials)

lazy val root = (project in file("."))
  .settings(name := "damavis-spark")
  .settings(settings)
  .settings(
    publishArtifact := false,
    excludeDependencies +="org.apache.hadoop" % "hadoop-client-api"
  )
  .aggregate(core, azure, snowflake)

lazy val core = (project in file("damavis-spark-core"))
  .settings(settings)
  .settings(name := "damavis-spark-core")
  .settings(
    crossScalaVersions := supportedScalaVersions,
    excludeDependencies +="org.apache.hadoop" % "hadoop-client-api"

  )

lazy val azure = (project in file("damavis-spark-azure"))
  .settings(settings)
  .settings(name := "damavis-spark-azure")
  .settings(
    crossScalaVersions := supportedScalaVersions,
    excludeDependencies +="org.apache.hadoop" % "hadoop-client-api"

  )
  .dependsOn(core)

lazy val snowflake = (project in file("damavis-spark-snowflake"))
  .settings(settings)
  .settings(name := "damavis-spark-snowflake")
  .settings(
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq("net.snowflake" %% "spark-snowflake" % "3.0.0"),
    excludeDependencies += "org.apache.hadoop" % "hadoop-client-api"

  )
  .dependsOn(core)
