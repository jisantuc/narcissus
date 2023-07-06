package io.github.jisantuc.narcissus

import cats.effect.{IO, Resource}
import cats.effect.std.Random
import feral.lambda._
import feral.lambda.events._
import feral.lambda.http4s._
import io.github.jisantuc.narcissus.HttpApp
import org.http4s._
import org.http4s.dsl.Http4sDsl
import natchez.xray.XRay

object lambdaHandler
    extends IOLambda[
      ApiGatewayProxyEventV2,
      ApiGatewayProxyStructuredResultV2
    ] {
  def handler: cats.effect.kernel.Resource[IO, LambdaEnv[
    IO,
    ApiGatewayProxyEventV2
  ] => IO[Option[ApiGatewayProxyStructuredResultV2]]] =
    for {
      xrayEntrypoint <- Resource
        .eval(Random.scalaUtilRandom[IO])
        .flatMap(implicit r => XRay.entryPoint[IO]())

    } yield {
      implicit env => // the LambdaEnv provides access to the event and context

        // a middleware to add tracing to any handler
        // it extracts the kernel from the event and adds tags derived from the context
        TracedHandler(xrayEntrypoint) { implicit trace =>
          val httpApp = new HttpApp[IO]

          // a "middleware" that converts an HttpRoutes into a ApiGatewayProxyHandler
          ApiGatewayProxyHandler(httpApp.routes)
        }
    }
}
