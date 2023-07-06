package io.github.jisantuc.narcissus.auth0

import scala.scalajs.js.annotation.JSImport

import scalajs.js

@js.native
@JSImport("@auth0/auth0-spa-js", "Auth0Client")
class Auth0Client extends js.Object {
  def isAuthenticated(): js.Promise[Boolean]     = js.native
  def loginWithRedirect(): js.Promise[Unit]      = js.native
  def handleRedirectCallback(): js.Promise[Unit] = js.native
  def getTokenSilently(): js.Promise[String]     = js.native
}
