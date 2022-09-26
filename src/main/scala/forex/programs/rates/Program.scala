package forex.programs.rates

//import cats.Functor
//import cats.data.EitherT
//import errors._
import forex.domain._
import forex.services.RatesService

class Program[F[_]](
    ratesService: RatesService[F]
) extends Algebra[F] {

  override def get(request: Protocol.GetRatesRequest): F[String] =
    //EitherT(ratesService.get(Rate.Pair(request.from, request.to))).leftMap(toProgramError(_)).value
    ratesService.getExchangeRate(Rate.Pair(request.from, request.to))
}

object Program {

  def apply[F[_]](
      ratesService: RatesService[F]
  ): Algebra[F] = new Program[F](ratesService)

}
