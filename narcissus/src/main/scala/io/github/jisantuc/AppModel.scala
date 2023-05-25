package io.github.jisantuc.narcissus

sealed abstract class AppModel

object AppModel:
  case object Unauthenticated             extends AppModel
  case class Authenticated(token: String) extends AppModel

  val unauthenticated: AppModel              = Unauthenticated
  def authenticated(token: String): AppModel = Authenticated(token)
