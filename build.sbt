import scala.sys.process._
import scala.language.postfixOps

import sbtwelcome._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := "3.2.2"

ThisBuild / version := "0.0.1"

ThisBuild / organization := "io.github.jisantuc"

ThisBuild / testFrameworks += new TestFramework("munit.Framework")

val Versions = new {
  val catsVersion          = "2.9.0"
  val circeVersion         = "0.14.5"
  val circeFs2Version      = "0.14.1"
  val feralVersion         = "0.2.2"
  val fs2Version           = "3.6.1"
  val http4sVersion        = "0.23.19"
  val ip4sVersion          = "3.3.0"
  val log4catsVersion      = "2.6.0"
  val natchezVersion       = "0.3.2"
  val natchezHttp4sVersion = "0.5.0"
  val skunkVersion         = "0.6.0"
}

val semanticDbSettings = Seq(
  semanticdbVersion := scalafixSemanticdb.revision,
  scalafixOnCompile := true,
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision,
  autoAPIMappings   := true
)

lazy val environment = settingKey[String](
  "The environment to bundle the application for. Used for interpolation of configuration values."
)

lazy val root = (project in file("."))
  .aggregate(
    narcissusDatamodel,
    narcissusApp,
    narcissusAPI,
    narcissusLocal,
    narcissusLambda
  )
  .settings( // Welcome message
    logo := "🌸 Narcissus (v" + version.value + ")",
    usefulTasks := Seq(
      UsefulTask(
        "fastOptJS",
        "Rebuild the JS (use during development)",
        UsefulTaskAlias.Empty,
        identity
      ),
      UsefulTask(
        "fullOptJS",
        "Rebuild the JS and optimise (use in production)",
        UsefulTaskAlias.Empty,
        identity
      )
    ),
    logoColor        := scala.Console.MAGENTA,
    aliasColor       := scala.Console.BLUE,
    commandColor     := scala.Console.CYAN,
    descriptionColor := scala.Console.WHITE
  )

lazy val narcissusDatamodel =
  (project in file("./datamodel"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      libraryDependencies ++= Seq(
        "io.circe"      %%% "circe-core" % Versions.circeVersion,
        "org.typelevel" %%% "cats-core"  % Versions.catsVersion,
        "org.scalameta" %%% "munit"      % "0.7.29" % Test
      ),
      semanticDbSettings
    )

lazy val narcissusApp =
  (project in file("./narcissus"))
    .enablePlugins(ScalaJSPlugin)
    .enablePlugins(BuildInfoPlugin)
    .dependsOn(narcissusDatamodel)
    .settings( // Normal settings
      name := "narcissus",
      libraryDependencies ++= Seq(
        "co.fs2"          %%% "fs2-core"     % Versions.fs2Version,
        "dev.optics"      %%% "monocle-core" % "3.2.0",
        "io.circe"        %%% "circe-fs2"    % Versions.circeFs2Version,
        "io.circe"        %%% "circe-parser" % Versions.circeVersion,
        "io.indigoengine" %%% "tyrian-io"    % "0.6.2",
        "org.scalameta"   %%% "munit"        % "0.7.29" % Test
      ),
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      buildInfoKeys    := Seq(auth0ClientId, auth0Domain, apiHost),
      buildInfoPackage := "io.github.jisantuc.narcissus",
      auth0ClientId := sys.env.getOrElse(
        "AUTH0_CLIENT_ID",
        throw new Exception("Missing AUTH0_CLIENT_ID environment variable")
      ),
      auth0Domain := sys.env.getOrElse(
        "AUTH0_DOMAIN",
        throw new Exception("Missing AUTH0_CLIENT_ID environment variable")
      ),
      apiHost := sys.env.getOrElse(
        "NARCISSUS_API_HOST",
        "localhost:8080"
      ),
      semanticDbSettings
    )

lazy val narcissusLambda = (project in file("./narcissus-lambda"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(LambdaJSPlugin)
  .dependsOn(narcissusAPI)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "feral-lambda-http4s" % Versions.feralVersion,
      "org.tpolecat"  %%% "natchez-xray"        % Versions.natchezVersion
    ),
    semanticDbSettings
  )

lazy val narcissusLocal = (project in file("./narcissus-local"))
  .dependsOn(narcissusAPI)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    libraryDependencies ++= Seq(
      "com.comcast"   %%% "ip4s-core"           % Versions.ip4sVersion,
      "org.tpolecat"  %%% "skunk-core"          % Versions.skunkVersion,
      "org.tpolecat"  %%% "natchez-log"         % Versions.natchezVersion,
      "org.typelevel" %%% "log4cats-core"       % Versions.log4catsVersion,
      "org.typelevel" %%% "log4cats-js-console" % Versions.log4catsVersion
    ),
    semanticDbSettings
  )

lazy val narcissusAPI =
  (project in file("./narcissus-api"))
    .enablePlugins(ScalaJSPlugin)
    .enablePlugins(LambdaJSPlugin)
    .dependsOn(narcissusDatamodel)
    .settings(
      name := "narcissus-api",
      libraryDependencies ++= Seq(
        "dev.optics" %%% "monocle-core"        % "3.2.0",
        "org.http4s" %%% "http4s-ember-server" % Versions.http4sVersion,
        "org.http4s" %%% "http4s-circe"        % Versions.http4sVersion,
        "org.http4s" %%% "http4s-dsl"          % Versions.http4sVersion,
        "org.tpolecat"  %%% "natchez-http4s" % Versions.natchezHttp4sVersion,
        "org.tpolecat"  %%% "skunk-core"     % Versions.skunkVersion,
        "org.scalameta" %%% "munit"          % "0.7.29" % Test
      ),
      semanticDbSettings
    )

lazy val auth0ClientId =
  settingKey[String]("The client id to use for authentication with Auth0")
    .withRank(KeyRanks.Invisible)

lazy val auth0Domain =
  settingKey[String]("The domain to use to authenticate with Auth0").withRank(
    KeyRanks.Invisible
  )

lazy val apiHost =
  settingKey[String](
    "The api host to use to communicate with the backend server"
  ).withRank(KeyRanks.Invisible)
