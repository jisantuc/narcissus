package io.github.jisantuc.narcissus.service

import scala.concurrent.duration._

import cats.data.NonEmptyList
import cats.effect.{Concurrent, Temporal}
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.{Applicative, ApplicativeError, Monad}
import io.circe.syntax._
import io.github.jisantuc.narcissus.Health
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import skunk.Session
import skunk.codec.boolean.bool
import skunk.implicits._

trait HealthCheck[F[_]: Monad] extends Http4sDsl[F] {
  def checkHealth: F[Health]

  def routes: HttpRoutes[F] = HttpRoutes.of[F] { case GET -> Root =>
    checkHealth flatMap {
      case h @ Health.Healthy      => Ok(h.asJson)
      case h @ Health.Unhealthy(_) => InternalServerError((h: Health).asJson)
    }
  }
}

object HealthCheck {
  def forSkunkSession[F[_]: Concurrent: Temporal](
      session: Session[F]
  ): HealthCheck[F] =
    new HealthCheck[F] {
      def checkHealth =
        Temporal[F].timeoutTo(
          session.unique(
            sql"select true".query(bool).map(_ => Health.Healthy)
          ),
          10.seconds,
          Applicative[F].pure(
            Health.Unhealthy(NonEmptyList.of("database timeout"))
          )
        )
    }
}
