package io.github.jisantuc.narcissus

import cats.syntax.either._
import io.circe.parser.decode
import io.circe.{Decoder => JsonDecoder}
import tyrian.http.HttpError.BadRequest
import tyrian.http.{Decoder, HttpError, Response}

object circeCompat:
  def decoderFor[T: JsonDecoder, Msg](
      onError: HttpError => Msg,
      onSuccess: T => Msg
  ): Decoder[Msg] = {
    val onResponse = { (response: Response) =>
      decode[T](response.body)
        .leftMap(e => BadRequest(e.getLocalizedMessage()))
        .fold(onError(_), onSuccess(_))
    }
    Decoder[Msg](onResponse, onError)
  }
