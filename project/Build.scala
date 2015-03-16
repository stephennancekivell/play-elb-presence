import sbt._
import sbt.Keys._

object ApplicationBuild extends Build {

  val appName = "play-elb-presence"

  val main = Project("play-elb-presence", file("."), settings = Defaults.defaultSettings).settings(
    name := appName,
    version := "0.1-SNAPSHOT",
    organization := "com.stephenn",
    scalaVersion := "2.10.4",
    crossScalaVersions := Seq("2.11.2", "2.10.4"),
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-java-sdk-elasticloadbalancing" % "1.9.22",
      "com.typesafe.play" %% "play" % "2.3.8",
      "com.typesafe.play" %% "play-ws" % "2.3.8"
    ),

    resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/maven-releases",

    // Publishing stuff
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>http://github.com/stephennancekivell/play-elb-presence</url>
        <licenses>
          <license>
            <name>MIT License</name>
            <url>http://opensource.org/licenses/MIT</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:stephennancekivell/play-elb-presence.git</url>
          <connection>scm:git:git@github.com:stephennancekivell/play-elb-presence.git</connection>
        </scm>
        <developers>
          <developer>
            <id>stephennancekivell</id>
            <name>Stephen Nancekivell</name>
            <url>http://github.com/stephennancekivell</url>
          </developer>
        </developers>)
  )
}
