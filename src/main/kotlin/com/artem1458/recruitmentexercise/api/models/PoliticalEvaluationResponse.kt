package com.artem1458.recruitmentexercise.api.models

import com.fasterxml.jackson.annotation.JsonProperty

data class PoliticalEvaluationResponse(
  @JsonProperty("mostSpeeches") val mostSpeeches: String?,
  @JsonProperty("mostSecurity") val mostSecurity: String?,
  @JsonProperty("leastWordy") val leastWordy: String?,
) {

  companion object {
    val empty = PoliticalEvaluationResponse(mostSpeeches = null, mostSecurity = null, leastWordy = null)
  }
}
