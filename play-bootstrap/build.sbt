import play.PlayImport.PlayKeys._

name := """play-bootstrap"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
routesImport += "binders._"

scalaVersion := "2.11.6"

resolvers += "sorm Scala 2.11 fork" at "http://markusjura.github.io/sorm"
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "webjars"  at "http://webjars.github.com/m2"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "1.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.0",
  "com.h2database" % "h2" % "1.4.177",
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  specs2 % Test
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := true
