package io.github.jisantuc.narcissus

import cats.data.NonEmptyList
import io.circe.Decoder.Result
import io.circe.syntax._
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor, Json}

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

  implicit val decoderHealth: Decoder[Health] = new Decoder[Health] {
    def apply(c: HCursor): Result[Health] =
      c.downField("healthy")
        .as[List[String]]
        .flatMap {
          case Nil => Right(Healthy)
          case _ =>
            Left(
              DecodingFailure("Reasons should be empty when healthy", c.history)
            )
        }
        .orElse(c.get[NonEmptyList[String]]("unhealthy").map(Unhealthy(_)))
  }
}
