package io.github.jisantuc.narcissus

import cats.Applicative
import cats.effect.{Concurrent, Temporal}
import io.github.jisantuc.narcissus.service.HealthCheck
import natchez.Trace
import natchez.http4s.NatchezMiddleware
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import skunk.Session

class HttpApp[F[_]: Concurrent: Temporal: Trace] extends Http4sDsl[F] {
  def routes(session: Session[F]) = {
    val httpRoutes = Router[F](
      "health" -> HealthCheck.forSkunkSession[F](session).routes
    )
    NatchezMiddleware.server(httpRoutes)
  }
}
