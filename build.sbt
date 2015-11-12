import com.typesafe.sbt.SbtAspectj._

name := "akka-streams-tut"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http-core-experimental" % "2.0-M1",
    "com.typesafe.akka" %% "akka-stream-experimental" % "2.0-M1",
    "org.reactivemongo" %% "reactivemongo" % "0.11.7",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.11.7.play24",
    "com.typesafe.play" % "play-json_2.11" % "2.4.3",
    "ch.qos.logback" % "logback-classic" % "1.1.3"
)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Typesafe" at "https://repo.typesafe.com/typesafe/releases"

mainClass in (Compile, run) := Some("com.fourgee.streams.http.server.Boot")
