package io.github.jisantuc.narcissus.command

import cats.effect.IO
import tyrian.Cmd

import scalajs.js
import scala.scalajs.js.annotation.JSImport
import io.github.jisantuc.narcissus.{AuthenticationSuccess, Msg}
import io.github.jisantuc.narcissus.auth0.Auth0Client
import io.github.jisantuc.narcissus.auth0.PureAuth0Client
import io.github.jisantuc.narcissus.BuildInfo
import org.scalajs.dom.window
import scala.scalajs.js.JSON

object auth:
  @js.native
  @JSImport("@auth0/auth0-spa-js", "createAuth0Client")
  private def createAuth0Client(
      clientOptions: js.Object
  ): js.Promise[Auth0Client] = js.native

  def authenticateCmd(clientId: String, domain: String, redirectUri: String): Cmd[IO, Msg] =
    val clientOptions = js.Dynamic.literal(
      clientId = clientId,
      domain = domain,
      // TODO -- this has to be configurable based on environment
      authorizationParams = js.Dynamic.literal(
        redirect_uri = redirectUri
      )
    )
    val query = window.location.search
    Cmd.Run(
      for {
        client <- IO.fromFuture(
          IO.delay(createAuth0Client(clientOptions).toFuture)
        )
        pureClient = new PureAuth0Client(client)
        isAuthenticated <- pureClient.isAuthenticated()
        _ <-
          if (isAuthenticated) pureClient.getTokenSilently()
          else if (query.contains("state=") && query.contains("code="))
            pureClient.handleRedirectCallback() <* IO.delay(
              window.history.replaceState((), "Narcissus", "/")
            ) >> pureClient.getTokenSilently()
          else
            pureClient.loginWithRedirect()
        token <- pureClient.getTokenSilently()
      } yield token,
      AuthenticationSuccess(_)
    )
