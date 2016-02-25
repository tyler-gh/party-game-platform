name := """party-game-platform"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb, SbtReactJs)


libraryDependencies ++= Seq(
  jdbc,
  "org.postgresql" % "postgresql" % "9.3-1103-jdbc4",
  "com.typesafe.play" %% "anorm" % "2.4.0",
  cache,
  ws,
  specs2 % Test,
  "org.yaml" % "snakeyaml" % "1.16"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

pipelineStages := Seq(concat)

Concat.groups := Seq(
  "index.js" -> group(Seq(
    "lib/rest.js",
    "lib/jquery-1.12.0.js",
    "lib/react/react.js",
    "lib/react/react-dom.min.js",
    "js/util/Api.js",
    "js/components/icons.js",
    "js/components/buttons.js",
    "js/components/background_color.js",
    "js/lobby/components/lobby_container.js",
    "js/lobby/components/lobby_countdown_timer.js",
    "js/components/forms.js",
    "js/components/game_banner.js",
    "js/lobby/game_lobby.js",
    "js/lobby/create_game.js",
    "js/lobby/join_game.js",
    "js/lobby/waiting_room.js",
    "js/lobby/game_select.js")
  ),
  "style.js" -> group(Seq(
    "lib/rest.js",
    "lib/jquery-1.12.0.js",
    "lib/react/react.js",
    "lib/react/react-dom.min.js",
    "js/components/icons.js",
    "js/components/buttons.js",
    "js/lobby/components/lobby_countdown_timer.js",
    "js/components/forms.js",
    "js/msc/styleguide.js")
  ),
  "game.js" -> group(Seq(
    "js/util/Api.js",
    "lib/jquery-1.12.0.js",
    "lib/react/react.js",
    "lib/react/react-dom.min.js",
    "js/components/icons.js",
    "js/components/buttons.js",
    "js/components/background_color.js",
    "js/components/game_banner.js",
    "js/lobby/components/lobby_container.js",
    "js/lobby/components/lobby_countdown_timer.js",
    "js/lobby/waiting_room.js",
    "js/game/game.js")
  )
)

Concat.parentDir := "public/main/js"

pipelineStages in Assets := Seq(concat)
