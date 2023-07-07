package io.github.jisantuc.narcissus

import cats.Applicative
import cats.effect.{Async, Concurrent, Temporal}
import fs2.io.file.Files
import io.github.jisantuc.narcissus.service.HealthCheck
import natchez.Trace
import natchez.http4s.NatchezMiddleware
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import skunk.Session

class HttpApp[F[_]: Async: Concurrent: Files: Temporal: Trace]
    extends Http4sDsl[F] {
  def routes(session: Session[F]) = {
    val httpRoutes = Router[F](
      "health" -> HealthCheck.forSkunkSession[F](session).routes
    )
    NatchezMiddleware.server(CORS.policy.withAllowOriginAll(httpRoutes))
  }
}
