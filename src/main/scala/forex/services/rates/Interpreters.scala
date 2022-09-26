package forex.services.rates

import cats.Applicative
import cats.effect.Concurrent
import interpreters._
import org.http4s.client.Client

object Interpreters {
  def dummy[F[_]: Applicative]: Algebra[F] = new OneFrameDummy[F]
  def oneFrame[F[_]: Concurrent](client: Client[F]): Algebra[F] = new OneFrame[F](client)
}
