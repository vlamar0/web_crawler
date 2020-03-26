import scala.io.Source
import scala.concurrent.ExecutionContext

object Crawler {
  def main(args: Array[String]): Unit = {
    implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
    val webCrawlingService = new WebCrawlingService()

    val domains: List[String] = Utils.using(Source.fromFile(Constants.INPUT_FILENAME))(_.getLines.toList)
    val domainBatches: List[List[String]] = Utils.split(domains, Constants.BATCH_SIZE)

    val writer = Utils.createCSVFile()
    for (batch <- domainBatches) {
      val crawlingResults = webCrawlingService.go(batch)
      Utils.writeToFile(writer, crawlingResults)
    }
    writer.close()
  }
}


