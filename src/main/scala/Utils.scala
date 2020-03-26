import java.io.{BufferedWriter, File, FileWriter}
import java.net.IDN

object Utils {
  def split[A](xs: List[A], n: Int): List[List[A]] = {
    if (xs.size <= n) xs :: Nil
    else (xs take n) :: split(xs drop n, n)
  }

  def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B = try { f(param) } finally { param.close() }

  def createCSVFile(): BufferedWriter = {
    val file = new File(Constants.OUTPUT_FILENAME)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(Constants.CSV_HEADER)
    bw
  }

  def writeToFile(bw: BufferedWriter, data: List[String]) = {
    for (line <- data) {
      bw.append(line)
    }
  }

  def toURIFormat(scheme: String, domain: String): String = {
    val encodedDomain = IDN.toASCII(domain)
    s"$scheme://$encodedDomain/"
  }

  def createCSVLine(
    domain: String,
    title: String = Constants.UNKNOWN,
    keywords: String = Constants.UNKNOWN,
    description: String = Constants.UNKNOWN,
    error: String = Constants.WO_ERROR
  ): String = {
    val siteTitle = if (title.isEmpty) Constants.UNKNOWN else title
    val metaKeywords = keywords.replaceAll("\n|\r\n", " ")
    val metaDescription = description.replaceAll("\n|\r\n", " ")
    s"$domain;$siteTitle;$metaKeywords;$metaDescription;$error\n"
  }
}
