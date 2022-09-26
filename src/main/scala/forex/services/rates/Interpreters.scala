package forex.services.rates

import cats.Applicative
import cats.effect.Concurrent
import forex.domain.{Currency, Price, Rate, Timestamp}
import forex.services.rates.errors.Error
import interpreters._
import io.circe.{Decoder, HCursor}
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import org.http4s.client.Client

import java.time.OffsetDateTime

object Interpreters {
  def dummy[F[_]: Applicative]: Algebra[F] = new OneFrameDummy[F]
  def oneFrame[F[_]: Concurrent](client: Client[F]): Algebra[F] = new OneFrame[F](client)

  implicit val rateDecoder: Decoder[Rate] = new Decoder[Rate] {
    final def apply(cursor: HCursor): Decoder.Result[Rate] =
      for {
        from <- cursor.downArray.downField("from").as[String]
        to <- cursor.downArray.downField("to").as[String]
        price <- cursor.downArray.downField("price").as[BigDecimal]
        offset <- cursor.downArray.downField("time_stamp").as[OffsetDateTime]
      } yield {
        val pair = Rate.Pair(Currency.fromString(from), Currency.fromString(to))
        Rate(pair, Price(price), Timestamp(offset))
      }
  }
  implicit def rateEntityDecoder[F[_] : Concurrent]: EntityDecoder[F, Rate] = jsonOf
  implicit val errorDecoder: Decoder[Error] = new Decoder[Error] {
    final def apply(cursor: HCursor): Decoder.Result[Error] =
      for {
        message <- cursor.downField("error").as[String]
      } yield (Error.OneFrameLookupFailed(message))
  }
  implicit def errorEntityDecoder[F[_] : Concurrent]: EntityDecoder[F, Error] = jsonOf
  implicit def eitherDecoder[A, B](implicit a: Decoder[A], b: Decoder[B]): Decoder[A Either B] = {
    val left: Decoder[Either[A, B]] = a.map(Left.apply)
    val right: Decoder[Either[A, B]] = b.map(Right.apply)
    left or right
  }
  implicit def eitherEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Error Either Rate] = jsonOf

}
