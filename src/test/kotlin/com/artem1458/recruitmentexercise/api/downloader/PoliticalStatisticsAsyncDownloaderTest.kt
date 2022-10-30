package com.artem1458.recruitmentexercise.api.downloader

import com.artem1458.recruitmentexercise.application.models.PoliticalStatistics
import com.artem1458.recruitmentexercise.exceptions.BadDataException
import com.artem1458.recruitmentexercise.testutils.BaseTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.util.concurrent.ExecutionException

@SpringBootTest
internal class PoliticalStatisticsAsyncDownloaderTest : BaseTest() {

  @Autowired
  private lateinit var downloader: IAsyncDownloader<List<PoliticalStatistics>>

  @Test
  fun `should download political statistics and parse it`() {
    // Given
    val url = mockk<URL>()
    val stream = Files.readString(Path.of("src/test/kotlin/resources/political_statistics.csv")).byteInputStream()
    val expected = listOf(
      PoliticalStatistics(
        speaker = "Artem",
        topic = "Education",
        date = LocalDate.of(2022, 1, 1),
        words = 1
      ),
      PoliticalStatistics(
        speaker = "Sonya",
        topic = "Programming",
        date = LocalDate.of(2022, 2, 2),
        words = 2
      ),
    )

    every { url.openStream() } returns stream

    // When
    val actual = downloader.download(url).get()

    // Then
    assertEquals(expected, actual)

    verify {
      url.openStream()
    }
  }

  @Test
  fun `should provide BadDataException when can not open stream for url`() {
    // Given
    val url = mockk<URL>()

    every { url.openStream() } throws IOException()

    // When
    val actual = downloader.download(url)

    // Then
    val exception = assertThrows<ExecutionException> { actual.get() }

    assertEquals("Can not proceed csv download", exception.cause?.message)
    assertInstanceOf(BadDataException::class.java, exception.cause)

    verify {
      url.openStream()
    }
  }

  @Test
  fun `should provide BadDataException when can not parse csv`() {
    // Given
    val url = mockk<URL>()
    val stream = """
      Speaker, Topic, Date, Words
      Artem, Education
    """.trimIndent().byteInputStream()

    every { url.openStream() } returns stream

    // When
    val actual = downloader.download(url)

    // Then
    val exception = assertThrows<ExecutionException> { actual.get() }

    assertEquals("Can not parse csv", exception.cause?.message)
    assertInstanceOf(BadDataException::class.java, exception.cause)

    verify {
      url.openStream()
    }
  }
}
