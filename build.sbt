name := """akka-streams"""

version := "1.0.0"

scalaVersion := "2.11.8"

resolvers += "OSS Sonatype" at "https://repo1.maven.org/maven2/"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % "2.4.14" withSources(),
    "com.typesafe.akka" % "akka-cluster_2.11" % "2.4.14" withSources()
  )
}