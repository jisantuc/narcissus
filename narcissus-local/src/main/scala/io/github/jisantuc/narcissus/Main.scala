package io.github.jisantuc.narcissus

import cats.effect.{ExitCode, IO, IOApp, Resource, ResourceApp}
import com.comcast.ip4s._
import natchez.Trace
import natchez.log.Log
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.console.ConsoleLogger
import org.typelevel.log4cats.extras.LogLevel
import skunk.Session

object Main extends IOApp {

  implicit val log: Logger[IO] = ConsoleLogger[IO](Some(LogLevel.Info))

  val logTracingEntrypoint = Log.entryPoint[IO]("narcissus-local", _.spaces2)

  def run(args: List[String]): IO[ExitCode] = (for {
    given Trace[IO] <- Resource.eval(
      Trace.ioTraceForEntryPoint(logTracingEntrypoint)
    )
    session <- Session.single[IO](
      host = "localhost",
      port = 5432,
      user = "narcissus",
      database = "narcissus",
      password = Some("narcissus")
    )
    application = HttpApp[IO].routes(session).orNotFound
    serverResult <-
      EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(application)
        .build
  } yield serverResult).useForever
}
