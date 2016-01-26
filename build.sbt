name := """party-game-platform"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb, SbtReactJs)


libraryDependencies ++= Seq(
  jdbc,
  "org.postgresql" % "postgresql" % "9.3-1103-jdbc4",
  cache,
  ws,
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

pipelineStages := Seq(concat)

Concat.groups := Seq(
  "game.js" -> group(Seq("lib/rest.js","lib/react/react.js","lib/react/react-dom.min.js","js/game_lobby.js", "js/game_select.js"))
)

Concat.parentDir := "public/main/js"

pipelineStages in Assets := Seq(concat)