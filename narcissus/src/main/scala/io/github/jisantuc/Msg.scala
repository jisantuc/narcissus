package io.github.jisantuc.narcissus

sealed abstract class Msg

case class AuthenticationSuccess(token: String) extends Msg
