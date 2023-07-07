package io.github.jisantuc.narcissus.pages

import io.github.jisantuc.narcissus.{CheckHealth, Msg}
import tyrian.Html
import tyrian.Html.*

case class AuthLanding(token: String) {
  def render: Html[Msg] = div(
    List(
      text(s"Token is: $token"),
      button(onClick(CheckHealth))("Check health")
    )
  )
}
