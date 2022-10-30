package com.artem1458.recruitmentexercise.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class JacksonConfiguration {

  @Bean
  @Primary
  fun objectMapper(): ObjectMapper = jacksonObjectMapper()

  @Bean
  fun csvMapper(): CsvMapper = CsvMapper().apply {
    enable(CsvParser.Feature.TRIM_SPACES)
    enable(CsvParser.Feature.SKIP_EMPTY_LINES)
    enable(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS)
    registerModule(JavaTimeModule())
  }
}
