import binders.Pager
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PagerSpec extends Specification {

  //persons?sorts[name]=1&perPage=50&queries[search]=asdf
  ///
  val path: String = "personsjs?queries%5Bsearch%5D=asdf&sorts%5Bname%5D=1&page=1&perPage=50&offset=0"

  "Pager" should {

    "get the proper queries attribute" in {
      val pager: Pager = Pager.fromString(path)
      pager.queries mustEqual Map(("search", "asdf"))
    }

    "get the perPage attribute" in {
      val pager: Pager = Pager.fromString(path)
      pager.perPage mustEqual 50
    }

    "get the sorts attribute" in {
      val pager: Pager = Pager.fromString(path)
      pager.sorts mustEqual Map(("name", "1"))
    }

    "get the offset attribute" in {
      val pager: Pager = Pager.fromString(path)
      pager.offset mustEqual 0
    }
  }
}
