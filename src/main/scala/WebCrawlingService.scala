import java.io.BufferedWriter
import java.util.concurrent.ArrayBlockingQueue

import org.jsoup.Jsoup

class WebCrawlingService {
  def go(domains: List[String], bw: BufferedWriter) = {
    val queue = new ArrayBlockingQueue[String](domains.size)

    val resultThreads: Seq[Thread] = domains.map { domain =>
      new Thread(new Runnable {
        def run {
          queue.put(retrieve(domain))
        }
      })
    }
    resultThreads.foreach(_.start())
    resultThreads.foreach(_.join())

    while (!queue.isEmpty) {
      bw.append(queue.take())
    }
  }

  def retrieve(domain: String): String = {
    try {
      val url = Utils.toURIFormat(Constants.HTTP_SCHEME, domain)

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