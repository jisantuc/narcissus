package io.github.jisantuc.narcissus

import cats.data.NonEmptyList
import io.circe.Encoder
import io.circe.syntax._
import io.circe.Json

sealed abstract class Health

object Health {
  case object Healthy                                extends Health
  case class Unhealthy(errors: NonEmptyList[String]) extends Health

  implicit val encoderHealth: Encoder[Health] = new Encoder[Health] {
    def apply(a: Health): Json = a match {
      case Unhealthy(reasons) =>
        Map("unhealthy" -> reasons).asJson
      case Healthy =>
        Map("healthy" -> List.empty[String]).asJson
    }
  }
}
