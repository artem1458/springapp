package com.artem1458.recruitmentexercise.application.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class PoliticalStatistics(
  @JsonProperty("Speaker") val speaker: String,
  @JsonProperty("Topic") val topic: String,
  @JsonProperty("Date") val date: LocalDate,
  @JsonProperty("Words") val words: Int,
)
