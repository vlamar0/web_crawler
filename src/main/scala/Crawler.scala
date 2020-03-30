import java.io.BufferedWriter

import scala.io.Source

object Crawler extends App {
  val webCrawlingService = new WebCrawlingService()

  val domains: List[String] = Utils.using(Source.fromFile(Constants.INPUT_FILENAME))(_.getLines.toList)
  val domainBatches: List[List[String]] = Utils.split(domains, Constants.BATCH_SIZE)

  val writer: BufferedWriter = Utils.createCSVFile()
  for (batch <- domainBatches) {
      webCrawlingService.go(batch, writer)
  }
  writer.close()
}
