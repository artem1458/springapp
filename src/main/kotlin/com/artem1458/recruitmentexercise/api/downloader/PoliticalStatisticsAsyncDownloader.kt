package com.artem1458.recruitmentexercise.api.downloader

import com.artem1458.recruitmentexercise.application.models.PoliticalStatistics
import com.artem1458.recruitmentexercise.exceptions.BadDataException
import com.artem1458.recruitmentexercise.utils.closeQuietly
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import org.springframework.scheduling.annotation.Async
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

open class PoliticalStatisticsAsyncDownloader(
  private val csvMapper: CsvMapper
) : IAsyncDownloader<List<PoliticalStatistics>> {

  private companion object {
    val CSV_SCHEMA: CsvSchema = CsvSchema.builder()
      .addColumn("Speaker")
      .addColumn("Topic")
      .addColumn("Date")
      .addColumn("Words")
      .setUseHeader(true)
      .build()
  }

  @Async
  override fun download(url: URL): Future<List<PoliticalStatistics>> {
    val stream = runCatching {
      BufferedInputStream(url.openStream())
    }.getOrElse {
      return CompletableFuture.failedFuture(BadDataException("Can not proceed csv download"))
    }

    return runCatching {
      val parsed = parseCSV(stream)

      CompletableFuture.completedFuture(parsed)
    }.getOrElse {
      return CompletableFuture.failedFuture(BadDataException("Can not parse csv"))
    }.also {
      stream.closeQuietly()
    }
  }

  private fun parseCSV(stream: InputStream): List<PoliticalStatistics> =
    csvMapper.readerFor(PoliticalStatistics::class.java)
      .with(CSV_SCHEMA)
      .readValues<PoliticalStatistics>(stream)
      .readAll()
}
