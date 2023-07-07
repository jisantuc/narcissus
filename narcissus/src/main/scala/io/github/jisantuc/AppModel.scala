package io.github.jisantuc.narcissus

sealed abstract class AppModel

object AppModel:
  case object Unauthenticated                   extends AppModel
  case class Authenticated[F[_]](token: String) extends AppModel

  val unauthenticated: AppModel = Unauthenticated
  def authenticated[F[_]](token: String): AppModel =
    Authenticated(token)
