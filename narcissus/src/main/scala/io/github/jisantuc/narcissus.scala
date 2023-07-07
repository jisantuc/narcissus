package io.github.jisantuc.narcissus

import scala.scalajs.js.annotation.*

import cats.effect.IO
import io.github.jisantuc.narcissus.AppModel.{Authenticated, Unauthenticated}
import io.github.jisantuc.narcissus.BuildInfo
import io.github.jisantuc.narcissus.command.auth
import monocle.syntax.all._
import tyrian.Html.*
import tyrian.*

@JSExportTopLevel("TyrianApp")
object narcissus extends TyrianApp[Msg, AppModel]:

  def init(flags: Map[String, String]): (AppModel, Cmd[IO, Msg]) =
    (
      AppModel.unauthenticated,
      auth.authenticateCmd(
        BuildInfo.auth0ClientId,
        BuildInfo.auth0Domain
      )
    )

  def update(model: AppModel): Msg => (AppModel, Cmd[IO, Msg]) = {
    case AuthenticationSuccess(token) =>
      (AppModel.authenticated(token), Cmd.None)
  }

  def view(model: AppModel): Html[Msg] =
    model match {
      case Authenticated(token) => div(s"Token is: $token")
      case Unauthenticated      => div("Unauthenticated")
    }

  def subscriptions(model: AppModel): Sub[IO, Msg] =
    Sub.None
