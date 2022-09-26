package forex.services.rates.interpreters

import cats.Applicative
import forex.domain.Rate
import forex.services.rates.{Algebra, errors}
import org.http4s.client.Client

class OneFrame[F[_]: Applicative](client: Client[F]) extends Algebra[F]{
  override def getExchangeRate(pair: Rate.Pair): F[String] = ???

  override def get(pair: Rate.Pair): F[Either[errors.Error, Rate]] = ???
}
