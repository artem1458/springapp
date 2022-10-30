package com.artem1458.recruitmentexercise.config

import com.artem1458.recruitmentexercise.api.downloader.IAsyncDownloader
import com.artem1458.recruitmentexercise.api.downloader.PoliticalStatisticsAsyncDownloader
import com.artem1458.recruitmentexercise.application.models.PoliticalStatistics
import com.artem1458.recruitmentexercise.application.usecases.GetPoliticalEvaluationUseCase
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync

@Configuration
@EnableAsync
class SpringConfiguration {

  @Bean
  fun politicalStatisticsDownloader(csvMapper: CsvMapper  ): IAsyncDownloader<List<PoliticalStatistics>> =
    PoliticalStatisticsAsyncDownloader(csvMapper)

  @Bean
  fun getPoliticalEvaluationUseCase() = GetPoliticalEvaluationUseCase()
}
