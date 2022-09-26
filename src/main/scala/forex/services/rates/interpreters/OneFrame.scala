package forex.services.rates.interpreters

import cats.effect.Concurrent
import forex.domain._
import forex.services.rates._
import forex.services.rates.errors._
import org.http4s.{Header, Headers, Request, Uri}
import org.http4s.client.Client
import org.http4s.Method.GET
import forex.services.rates.Interpreters.eitherEntityDecoder

class OneFrame[F[_]: Concurrent](client: Client[F]) extends Algebra[F]{
  override def getExchangeRate(pair: Rate.Pair): F[Error Either Rate] ={
    val request =
      Request[F](
        method = GET,
        uri = Uri.unsafeFromString(s"http://0.0.0.0:8080/rates?pair=${pair.from}${pair.to}"),
        headers = Headers.of(Header("token", "10dc303535874aeccc86a8251e6992f5"))
      )
    client.expect[Error Either Rate](request)
  }



  override def get(pair: Rate.Pair): F[Either[errors.Error, Rate]] = ???
}
