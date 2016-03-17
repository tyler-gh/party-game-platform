name := """party-game-platform"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  jdbc,
  "org.postgresql" % "postgresql" % "9.3-1103-jdbc4",
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "io.bit3" % "jsass" % "4.1.0",
  cache,
  ws,
  specs2 % Test,
  "org.yaml" % "snakeyaml" % "1.16",
  "com.typesafe" %% "jse" % "1.1.2",
  "org.slf4j" % "slf4j-simple" % "1.7.6"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

routesGenerator := InjectedRoutesGenerator
