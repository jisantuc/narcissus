package io.github.jisantuc.narcissus.auth0

import cats.effect.IO

import scalajs.js

class PureAuth0Client(baseClient: Auth0Client) {
  def isAuthenticated()              = toIO(baseClient.isAuthenticated())
  def loginWithRedirect()            = toIO(baseClient.loginWithRedirect())
  def handleRedirectCallback()       = toIO(baseClient.handleRedirectCallback())
  def getTokenSilently(): IO[String] = toIO(baseClient.getTokenSilently())

  private def toIO[T](prom: js.Promise[T]): IO[T] =
    IO.fromFuture(IO.delay(prom.toFuture))
}
