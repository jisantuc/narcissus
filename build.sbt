import scala.sys.process._
import scala.language.postfixOps

import sbtwelcome._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

val Versions = new {
  val circeFs2Version = "0.14.1"
  val fs2Version      = "3.6.1"
}

lazy val narcissus =
  (project in file("."))
    .enablePlugins(ScalaJSPlugin)
    .settings( // Normal settings
      name         := "narcissus",
      version      := "0.0.1",
      scalaVersion := "3.2.1",
      organization := "io.github.jisantuc",
      libraryDependencies ++= Seq(
        "dev.optics"      %%% "monocle-core" % "3.2.0",
        "io.indigoengine" %%% "tyrian-io"    % "0.6.1",
        "co.fs2"          %%% "fs2-core"     % Versions.fs2Version,
        "io.circe"        %%% "circe-fs2"    % Versions.circeFs2Version,
        "org.scalameta"   %%% "munit"        % "0.7.29" % Test
      ),
      testFrameworks += new TestFramework("munit.Framework"),
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      scalafixOnCompile := true,
      semanticdbEnabled := true,
      semanticdbVersion := scalafixSemanticdb.revision,
      autoAPIMappings   := true
    )
    .settings( // Welcome message
      logo := "🌸 Narcissus (v" + version.value + ")",
      usefulTasks := Seq(
        UsefulTask("fastOptJS", "Rebuild the JS (use during development)"),
        UsefulTask(
          "fullOptJS",
          "Rebuild the JS and optimise (use in production)"
        ),
      ),
      logoColor        := scala.Console.MAGENTA,
      aliasColor       := scala.Console.BLUE,
      commandColor     := scala.Console.CYAN,
      descriptionColor := scala.Console.WHITE
    )
