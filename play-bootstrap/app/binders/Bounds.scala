package binders

import play.api.mvc.QueryStringBindable

case class Bounds(min: Int, max: Int)

object Bounds {
  implicit def binder(implicit intBinder: QueryStringBindable[Int]) = new QueryStringBindable[Bounds] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Bounds]] = {
      for {
        minE <- intBinder.bind(key + ".min", params)
        maxE <- intBinder.bind(key + ".max", params)
      } yield {
        (minE, maxE) match {
          case (Right(min), Right(max)) if min <= max => Right(Bounds(min, max))
          case _ => Left("Unable to bind bounds")
        }
      }
    }

    def unbind(key: String, bounds: Bounds) = {
      //      "queries[search]=asdf&perPage=100&sorts[name]=0"
      key + ".min=" + intBinder.unbind(key, bounds.min) + "&" + key + ".max=" + intBinder.unbind(key, bounds.max)
    }

    //      key + ".min=" + intBinder.unbind(bounds.min) + "&" + key + ".max=" + intBinder.unbind(bounds.max)
  }
}