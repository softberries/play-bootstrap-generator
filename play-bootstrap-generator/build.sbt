name := """play-boostrap-generator"""

version := "1.0"

scalaVersion := "2.11.0"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "repo.codahale.com" at "http://repo.codahale.com"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.1.3" % "test",
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.1",
  "org.scala-sbt" % "io" % "0.13.0",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.scalatra.scalate" % "scalate-core_2.11" % "1.7.0"
)