package io.github.jisantuc.narcissus

sealed abstract class Msg

case class AuthenticationSuccess[F[_]](token: String) extends Msg

case object CheckHealth       extends Msg
case object CheckedHealth     extends Msg
case object CheckHealthFailed extends Msg
