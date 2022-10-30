package com.artem1458.recruitmentexercise.application.usecases

import com.artem1458.recruitmentexercise.application.models.PoliticalStatistics
import com.artem1458.recruitmentexercise.exceptions.BadDataException
import com.artem1458.recruitmentexercise.testutils.BaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

internal class GetPoliticalEvaluationUseCaseTest : BaseTest() {

  private val useCase = GetPoliticalEvaluationUseCase()

  @Nested
  inner class GetPoliticWithMostSpeechesByYearTest {

    @Test
    fun `should get politic with most speeches by year`() {
      // Given
      val currentYear = LocalDate.now().year
      val previousYear = currentYear - 1
      val speaker1 = random.nextString()
      val speaker2 = random.nextString()
      val statistics = listOf(
        buildStatistics(
          speaker = speaker1,
          date = LocalDate.of(currentYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker1,
          date = LocalDate.of(currentYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker1,
          date = LocalDate.of(previousYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker2,
          date = LocalDate.of(currentYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker2,
          date = LocalDate.of(previousYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker2,
          date = LocalDate.of(previousYear, 1, 1),
        ),
      )
      val expected = speaker1

      // When
      val actual = useCase.getPoliticWithMostSpeeches(year = currentYear, statistics = statistics)

      // Then
      assertEquals(expected, actual)
    }

    @Test
    fun `should get null when has no any speeches in target year`() {
      // Given
      val currentYear = LocalDate.now().year
      val previousYear = currentYear - 1
      val speaker1 = random.nextString()
      val speaker2 = random.nextString()
      val statistics = listOf(
        buildStatistics(
          speaker = speaker1,
          date = LocalDate.of(previousYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker2,
          date = LocalDate.of(previousYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker2,
          date = LocalDate.of(previousYear, 1, 1),
        ),
      )

      // When
      val actual = useCase.getPoliticWithMostSpeeches(year = currentYear, statistics = statistics)

      // Then
      assertNull(actual)
    }

    @Test
    fun `should get null when 2 or more speakers has same amount of speeches`() {
      // Given
      val currentYear = LocalDate.now().year
      val previousYear = currentYear - 1
      val speaker1 = random.nextString()
      val speaker2 = random.nextString()
      val statistics = listOf(
        buildStatistics(
          speaker = speaker1,
          date = LocalDate.of(currentYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker1,
          date = LocalDate.of(currentYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker1,
          date = LocalDate.of(previousYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker2,
          date = LocalDate.of(currentYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker2,
          date = LocalDate.of(currentYear, 1, 1),
        ),
        buildStatistics(
          speaker = speaker2,
          date = LocalDate.of(previousYear, 1, 1),
        ),
      )

      // When
      val actual = useCase.getPoliticWithMostSpeeches(year = currentYear, statistics = statistics)

      // Then
      assertNull(actual)
    }

    @Test
    fun `should get null when statistics is empty list`() {
      // Given
      val statistics = emptyList<PoliticalStatistics>()

      // When
      val actual = useCase.getPoliticWithMostSpeeches(year = random.nextInt(), statistics)

      // Then
      assertNull(actual)
    }
  }

  @Nested
  inner class GetPoliticWithMostSpeechesByTopicTest {

    @Test
    fun `should get politic with most speeches by topic`() {
      // Given
      val topic1 = random.nextString()
      val topic2 = random.nextString()
      val speaker1 = random.nextString()
      val speaker2 = random.nextString()
      val statistics = listOf(
        buildStatistics(
          speaker = speaker1,
          topic = topic1
        ),
        buildStatistics(
          speaker = speaker1,
          topic = topic1
        ),
        buildStatistics(
          speaker = speaker1,
          topic = topic2
        ),
        buildStatistics(
          speaker = speaker2,
          topic = topic1
        ),
        buildStatistics(
          speaker = speaker2,
          topic = topic2
        ),
      )
      val expected = speaker1

      // When
      val actual = useCase.getPoliticWithMostSpeeches(topic = topic1, statistics = statistics)

      // Then
      assertEquals(expected, actual)
    }

    @Test
    fun `should get null when has not speeches with target topic`() {
      // Given
      val topic1 = random.nextString()
      val topic2 = random.nextString()
      val speaker1 = random.nextString()
      val speaker2 = random.nextString()
      val statistics = listOf(
        buildStatistics(
          speaker = speaker1,
          topic = topic2
        ),
        buildStatistics(
          speaker = speaker1,
          topic = topic2
        ),
        buildStatistics(
          speaker = speaker2,
          topic = topic2
        ),
      )

      // When
      val actual = useCase.getPoliticWithMostSpeeches(topic = topic1, statistics = statistics)

      // Then
      assertNull(actual)
    }

    @Test
    fun `should get null when 2 or more speakers has same amount of speeches with target topic`() {
      // Given
      val topic1 = random.nextString()
      val topic2 = random.nextString()
      val speaker1 = random.nextString()
      val speaker2 = random.nextString()
      val statistics = listOf(
        buildStatistics(
          speaker = speaker1,
          topic = topic1
        ),
        buildStatistics(
          speaker = speaker1,
          topic = topic1
        ),
        buildStatistics(
          speaker = speaker1,
          topic = topic2
        ),
        buildStatistics(
          speaker = speaker2,
          topic = topic1
        ),
        buildStatistics(
          speaker = speaker2,
          topic = topic1
        ),
        buildStatistics(
          speaker = speaker2,
          topic = topic2
        ),
      )

      // When
      val actual = useCase.getPoliticWithMostSpeeches(topic = topic1, statistics = statistics)

      // Then
      assertNull(actual)
    }

    @Test
    fun `should get null when statistics is empty list`() {
      // Given
      val statistics = emptyList<PoliticalStatistics>()

      // When
      val actual = useCase.getPoliticWithMostSpeeches(topic = random.nextString(), statistics)

      // Then
      assertNull(actual)
    }
  }

  @Nested
  inner class GetLeastWordyPoliticTest {

    @Test
    fun `should get least wordy politic`() {
      // Given
      val speaker1 = random.nextString()
      val speaker2 = random.nextString()
      val statistics = listOf(
        buildStatistics(
          speaker = speaker1,
          words = 1,
        ),
        buildStatistics(
          speaker = speaker1,
          words = 1,
        ),
        buildStatistics(
          speaker = speaker1,
          words = 1,
        ),
        buildStatistics(
          speaker = speaker2,
          words = 1,
        ),
        buildStatistics(
          speaker = speaker2,
          words = 2,
        ),
        buildStatistics(
          speaker = speaker2,
          words = 3,
        ),
      )
      val expected = speaker1

      // When
      val actual = useCase.getLeastWordyPolitic(statistics)

      // Then
      assertEquals(expected, actual)
    }

    @Test
    fun `should get null when 2 or more speakers has same minimal amount of sayed words`() {
      // Given
      val speaker1 = random.nextString()
      val speaker2 = random.nextString()
      val statistics = listOf(
        buildStatistics(
          speaker = speaker1,
          words = 2,
        ),
        buildStatistics(
          speaker = speaker2,
          words = 2,
        ),
      )

      // When
      val actual = useCase.getLeastWordyPolitic(statistics)

      // Then
      assertNull(actual)
    }

    @Test
    fun `should get null when statistics is empty list`() {
      // Given
      val statistics = emptyList<PoliticalStatistics>()

      // When
      val actual = useCase.getLeastWordyPolitic(statistics)

      // Then
      assertNull(actual)
    }

    @Test
    fun `should throw BadDataException when words is negative integer`() {
      // Given
      val statistics = listOf(
        buildStatistics(words = 0),
        buildStatistics(words = 1),
        buildStatistics(words = -1),
      )
      val expected = "Politician words count can not be negative"

      // When - Then
      val exception = assertThrows<BadDataException> { useCase.getLeastWordyPolitic(statistics) }

      assertEquals(expected, exception.message)
    }
  }

  private fun buildStatistics(
    speaker: String = random.nextString(),
    topic: String = random.nextString(),
    date: LocalDate = LocalDate.now().minusDays(random.nextInt(1000).toLong()),
    words: Int = random.nextInt(0, 1000)
  ) = random.build<PoliticalStatistics>().copy(
    speaker = speaker,
    topic = topic,
    date = date,
    words = words
  )
}
