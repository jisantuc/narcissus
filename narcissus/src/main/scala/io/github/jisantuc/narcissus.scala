package io.github.jisantuc.narcissus

import cats.effect.IO
import monocle.syntax.all._
import tyrian.Html.*
import tyrian.*

import scala.scalajs.js.annotation.*
import _root_.io.github.jisantuc.narcissus.command.auth
import io.github.jisantuc.narcissus.AppModel.Authenticated
import io.github.jisantuc.narcissus.AppModel.Unauthenticated

@JSExportTopLevel("TyrianApp")
object narcissus extends TyrianApp[Msg, AppModel]:

  def init(flags: Map[String, String]): (AppModel, Cmd[IO, Msg]) =
    (
      AppModel.unauthenticated,
      auth.authenticateCmd(
        "MepGXAaIFJztjvBGIR1bToDMqXJjpxhk",
        "narcissus.us.auth0.com"
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
