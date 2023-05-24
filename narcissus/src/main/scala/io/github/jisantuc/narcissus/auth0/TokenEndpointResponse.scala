package io.github.jisantuc.narcissus.auth0

import scalajs.js

trait TokenEndpointResponse extends js.Object {
  val access_token: String

  val expires_in: Int
}
