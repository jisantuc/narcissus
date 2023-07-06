package io.github.jisantuc.narcissus.command

import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSImport

import cats.effect.IO
import io.github.jisantuc.narcissus.auth0.{Auth0Client, PureAuth0Client}
import io.github.jisantuc.narcissus.{AuthenticationSuccess, BuildInfo, Msg}
import org.scalajs.dom.window
import tyrian.Cmd

import scalajs.js

object auth:
  @js.native
  @JSImport("@auth0/auth0-spa-js", "createAuth0Client")
  private def createAuth0Client(
      clientOptions: js.Object
  ): js.Promise[Auth0Client] = js.native

  def authenticateCmd(clientId: String, domain: String): Cmd[IO, Msg] =
    val clientOptions = js.Dynamic.literal(
      clientId = clientId,
      domain = domain,
      authorizationParams = js.Dynamic.literal(
        redirect_uri = window.location.toString()
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
