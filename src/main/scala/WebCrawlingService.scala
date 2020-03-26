import org.jsoup.Jsoup

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

class WebCrawlingService()(implicit val ec: ExecutionContext) {
  def go(domains: List[String]): List[String] = {
    val futureResults: List[Future[String]] = domains.map { domain =>
      retrieve(domain)
    }
    Await.result(Future.sequence(futureResults), Duration.Inf)
  }

  def retrieve(domain: String): Future[String] = Future {
    val url = Utils.toURIFormat(Constants.HTTP_SCHEME, domain)
    try {
      val doc = Jsoup.connect(url).maxBodySize(Constants.SIZE_LIMIT).timeout(Constants.TIME_LIMIT).get
      val metaKeywords: String = try {
        doc.select("meta[name=keywords]").first().attr("content")
      } catch {
        case _: NullPointerException => Constants.MISSING
      }
      val metaDescription: String = try {
        doc.select("meta[name=description]").first().attr("content")
      } catch {
        case _: NullPointerException => Constants.MISSING
      }
      Utils.createCSVLine(domain, doc.title, metaKeywords, metaDescription)
    } catch {
      case e: Throwable => Utils.createCSVLine(domain, error = e.getMessage)
    }
  }
}