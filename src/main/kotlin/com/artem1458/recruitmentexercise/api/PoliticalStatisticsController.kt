package com.artem1458.recruitmentexercise.api

import com.artem1458.recruitmentexercise.api.downloader.IAsyncDownloader
import com.artem1458.recruitmentexercise.api.models.PoliticalEvaluationResponse
import com.artem1458.recruitmentexercise.application.models.PoliticalStatistics
import com.artem1458.recruitmentexercise.application.usecases.GetPoliticalEvaluationUseCase
import com.artem1458.recruitmentexercise.exceptions.BadDataException
import com.artem1458.recruitmentexercise.utils.collect
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URL

@RestController
class PoliticalStatisticsController(
  private val politicalStatisticsDownloader: IAsyncDownloader<List<PoliticalStatistics>>,
  private val getPoliticalEvaluationUseCase: GetPoliticalEvaluationUseCase
) {

  private companion object {
    val ALLOWED_PROTOCOLS = setOf("http", "https")

    const val EVALUATION_YEAR = 2013
    const val SECURITY_TOPIC_NAME = "Internal Security"
  }

  @GetMapping("/evaluation")
  fun getEvaluation(
    @RequestParam(name = "url", required = false, defaultValue = "") requestUrls: Set<String> = emptySet()
  ): PoliticalEvaluationResponse {
    if (requestUrls.isEmpty())
      return PoliticalEvaluationResponse.empty

    val urls = parseAndValidateUrls(requestUrls)

    val statistics = runCatching {
      urls.map(politicalStatisticsDownloader::download).collect().flatten()
    }.getOrElse {
      throw it.cause ?: it
    }

    return PoliticalEvaluationResponse(
      mostSpeeches = getPoliticalEvaluationUseCase.getPoliticWithMostSpeeches(
        year = EVALUATION_YEAR,
        statistics = statistics
      ),
      mostSecurity = getPoliticalEvaluationUseCase.getPoliticWithMostSpeeches(
        topic = SECURITY_TOPIC_NAME,
        statistics = statistics
      ),
      leastWordy = getPoliticalEvaluationUseCase.getLeastWordyPolitic(statistics)
    )
  }

  private fun parseAndValidateUrls(urls: Iterable<String>): List<URL> = runCatching {
    urls.map(::URL)
  }.onFailure {
    throw BadDataException("Can not parse url")
  }.getOrThrow()
    .also { parsedUrls ->
      if (parsedUrls.any { it.protocol.lowercase() !in ALLOWED_PROTOCOLS })
        throw BadDataException("Only '${ALLOWED_PROTOCOLS.joinToString()}' urls are allowed")
    }
}
