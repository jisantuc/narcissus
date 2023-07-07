package io.github.jisantuc.narcissus.command

import cats.effect.kernel.Async
import io.circe.{Decoder => JsonDecoder}
import io.github.jisantuc.narcissus.{
  BuildInfo,
  CheckHealthFailed,
  CheckedHealth,
  Health,
  Msg,
  circeCompat
}
import tyrian.http.{Decoder, Http, Request}

object healthCheck:
  def healthCheckCmd[F[_]: Async] =
    Http.send[F, Health, Msg](
      Request.get(s"http://${BuildInfo.apiHost}/health"),
      circeCompat.decoderFor[Health, Msg](
        _ => CheckedHealth,
        _ => CheckHealthFailed
      )
    )
