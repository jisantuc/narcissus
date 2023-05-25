import scala.sys.process._
import scala.language.postfixOps

import sbtwelcome._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := "3.2.1"

ThisBuild / version := "0.0.1"

ThisBuild / organization := "io.github.jisantuc"

ThisBuild / testFrameworks += new TestFramework("munit.Framework")

val Versions = new {
  val circeFs2Version = "0.14.1"
  val fs2Version      = "3.6.1"
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
  .aggregate(narcissusDatamodel, narcissusApp)
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
        "org.scalameta" %%% "munit" % "0.7.29" % Test
      )
    )
    .settings(semanticDbSettings)

lazy val narcissusApp =
  (project in file("./narcissus"))
    .enablePlugins(ScalaJSPlugin)
    .enablePlugins(BuildInfoPlugin)
    .settings( // Normal settings
      name := "narcissus",
      libraryDependencies ++= Seq(
        "dev.optics"      %%% "monocle-core" % "3.2.0",
        "io.indigoengine" %%% "tyrian-io"    % "0.6.2",
        "co.fs2"          %%% "fs2-core"     % Versions.fs2Version,
        "io.circe"        %%% "circe-fs2"    % Versions.circeFs2Version,
        "org.scalameta"   %%% "munit"        % "0.7.29" % Test
      ),
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      buildInfoKeys    := Seq(auth0ClientId, auth0Domain),
      buildInfoPackage := "io.github.jisantuc.narcissus",
      auth0ClientId := sys.env.getOrElse(
        "AUTH0_CLIENT_ID",
        throw new Exception("Missing AUTH0_CLIENT_ID environment variable")
      ),
      auth0Domain := sys.env.getOrElse(
        "AUTH0_DOMAIN",
        throw new Exception("Missing AUTH0_CLIENT_ID environment variable")
      ),
      semanticDbSettings
    )

lazy val auth0ClientId =
  settingKey[String]("The client id to use for authentication with Auth0")

lazy val auth0Domain =
  settingKey[String]("The domain to use to authenticate with Auth0")
