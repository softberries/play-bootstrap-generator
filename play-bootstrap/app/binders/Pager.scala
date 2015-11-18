package binders

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

import scala.util.matching.Regex

case class Pager(queries: Map[String, String] = Map.empty, perPage: Int = 10, sorts: Map[String, String] = Map.empty, offset: Int = 0)

object Pager {

  ////persons?sorts[name]=1&perPage=50&queries[search]=asdf&blahblah
  def fromString(path: String): Pager = {
    println("pager string: " + path)
    val decodedPath: String = URLDecoder.decode(path, StandardCharsets.UTF_8.toString)
    println("decoded: " + decodedPath)
    Pager(
      queries = mapValue(decodedPath, "queries"),
      perPage = stringValue(decodedPath, "perPage", "10").toInt,
      sorts = mapValue(decodedPath, "sorts"),
      offset = stringValue(decodedPath, "offset", "0").toInt
    )
  }

  def mapValue(query: String, searchString: String): Map[String, String] = {
    val pattern = new Regex(searchString + "\\[(.*)\\]=(.*)")
    findQueryEntity(query, searchString) match {
      case Some(s) => {
        val matches = pattern findFirstMatchIn s
        Map((matches.get.group(1), matches.get.group(2)))
      }
      case _ => {
        println("no matches found for " + searchString)
        Map.empty
      }
    }
  }

  def stringValue(query: String, searchString: String, defaultValue: String): String = {
    val pattern = new Regex(searchString + "=(.*)")
    findQueryEntity(query, searchString) match {
      case Some(s) => {
        val matches = pattern findFirstMatchIn s
        matches.get.group(1)
      }
      case _ => {
        println("no matches found for " + searchString)
        defaultValue
      }
    }
  }

  def findQueryEntity(query: String, entityName: String): Option[String] = {
    query.split("&").filter(_.contains(entityName)).headOption
  }
}