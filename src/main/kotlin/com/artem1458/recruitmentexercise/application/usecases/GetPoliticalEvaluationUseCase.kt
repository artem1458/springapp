package com.artem1458.recruitmentexercise.application.usecases

import com.artem1458.recruitmentexercise.application.models.PoliticalStatistics
import com.artem1458.recruitmentexercise.exceptions.BadDataException
import com.artem1458.recruitmentexercise.utils.invert

class GetPoliticalEvaluationUseCase {

  fun getPoliticWithMostSpeeches(year: Int, statistics: List<PoliticalStatistics>): String? {
    val statisticsByYear = statistics.filter { it.date.year == year }

    return getSpeakerWithMaxSpeechesCount(statisticsByYear)
  }

  fun getPoliticWithMostSpeeches(topic: String, statistics: List<PoliticalStatistics>): String? {
    val statisticsByTopic = statistics.filter { it.topic == topic }

    return getSpeakerWithMaxSpeechesCount(statisticsByTopic)
  }

  fun getLeastWordyPolitic(statistics: List<PoliticalStatistics>): String? {
    validateStatistics(statistics)

    val speakerToWordsTotal = statistics
      .groupBy { it.speaker }
      .mapValues { (_, statistics) ->
        statistics.sumOf { it.words }
      }

    return getSpeakerWithMinCount(speakerToWordsTotal.invert())
  }

  private fun getSpeakerWithMaxSpeechesCount(statistics: List<PoliticalStatistics>): String? {
    val speakerToStatistics = statistics.groupBy { it.speaker }
    val speakerToSpeechesCount = speakerToStatistics.mapValues { (_, statistics) ->
      statistics.size
    }

    return getSpeakerWithMaxCount(speakerToSpeechesCount.invert())
  }

  private fun getSpeakerWithMaxCount(countToSpeakers: Map<Int, List<String>>): String? {
    val speakersWithMaxCount = countToSpeakers.maxByOrNull { it.key }?.value
      ?: return null

    if (speakersWithMaxCount.size > 1)
      return null

    return speakersWithMaxCount.first()
  }

  private fun getSpeakerWithMinCount(countToSpeakers: Map<Int, List<String>>): String? {
    val speakersWithMinCount = countToSpeakers.minByOrNull { it.key }?.value
      ?: return null

    if (speakersWithMinCount.size > 1)
      return null

    return speakersWithMinCount.first()
  }

  private fun validateStatistics(statistics: List<PoliticalStatistics>) {
    if (statistics.any { it.words < 0 })
      throw BadDataException("Politician words count can not be negative")
  }
}
