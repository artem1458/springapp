package com.artem1458.recruitmentexercise.api

import com.artem1458.recruitmentexercise.api.downloader.IAsyncDownloader
import com.artem1458.recruitmentexercise.api.models.PoliticalEvaluationResponse
import com.artem1458.recruitmentexercise.application.models.PoliticalStatistics
import com.artem1458.recruitmentexercise.application.usecases.GetPoliticalEvaluationUseCase
import com.artem1458.recruitmentexercise.exceptions.BadDataException
import com.artem1458.recruitmentexercise.testutils.BaseTest
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.net.URL
import java.util.concurrent.CompletableFuture

@WebMvcTest(PoliticalStatisticsController::class)
@ExtendWith(SpringExtension::class)
internal class PoliticalStatisticsControllerTest : BaseTest() {

  @MockkBean
  private lateinit var politicalStatisticsDownloader: IAsyncDownloader<List<PoliticalStatistics>>

  @MockkBean
  private lateinit var getPoliticalEvaluationUseCase: GetPoliticalEvaluationUseCase

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var objectMapper: ObjectMapper

  @AfterEach
  fun afterEach() {
    confirmVerified(
      politicalStatisticsDownloader,
      getPoliticalEvaluationUseCase
    )
  }

  @Nested
  inner class Evaluation {

    @Test
    fun `should return political evaluation statistics`() {
      // Given
      val stringUrls = listOf(
        "http://mock1",
        "http://mock1",
        "https://mock2",
        "https://mock2",
      )
      val expectedUrls = listOf(
        URL(stringUrls[0]),
        URL(stringUrls[2]),
      )

      val politicalStatistics = listOf<List<PoliticalStatistics>>(
        random.buildList(2),
        random.buildList(2),
      )
      val downloadedStatistics = listOf(
        CompletableFuture.completedFuture(politicalStatistics[0]),
        CompletableFuture.completedFuture(politicalStatistics[1]),
      )
      val expectedStatistics = politicalStatistics.flatten()

      val mostSpeeches = random.nextString()
      val mostSecurity = random.nextString()
      val leastWordy = random.nextString()

      val expected = PoliticalEvaluationResponse(
        mostSpeeches = mostSpeeches,
        mostSecurity = mostSecurity,
        leastWordy = leastWordy
      )
      val expectedJson = objectMapper.writeValueAsString(expected)

      every { politicalStatisticsDownloader.download(any()) } returnsMany downloadedStatistics
      every { getPoliticalEvaluationUseCase.getPoliticWithMostSpeeches(any<Int>(), any()) } returns mostSpeeches
      every { getPoliticalEvaluationUseCase.getPoliticWithMostSpeeches(any<String>(), any()) } returns mostSecurity
      every { getPoliticalEvaluationUseCase.getLeastWordyPolitic(any()) } returns leastWordy

      // When
      val response = mockMvc.perform(
        MockMvcRequestBuilders.get("/evaluation")
          .queryParam("url", *stringUrls.toTypedArray())
      )

      // Then
      response.andExpect {
        status().isOk
        content().json(expectedJson)
      }

      verify {
        politicalStatisticsDownloader.download(expectedUrls[0])
        politicalStatisticsDownloader.download(expectedUrls[1])
        getPoliticalEvaluationUseCase.getPoliticWithMostSpeeches(2013, expectedStatistics)
        getPoliticalEvaluationUseCase.getPoliticWithMostSpeeches("Internal Security", expectedStatistics)
        getPoliticalEvaluationUseCase.getLeastWordyPolitic(expectedStatistics)
      }
    }

    @Test
    fun `should return empty response object when have no urls provided`() {
      // Given
      val expected = objectMapper.writeValueAsString(PoliticalEvaluationResponse.empty)

      // When
      val response = mockMvc.perform(MockMvcRequestBuilders.get("/evaluation"))

      // Then
      response.andExpect {
        status().isOk
        content().json(expected)
      }
    }

    @Test
    fun `should return 400 when can not parse urls`() {
      // Given
      val url = random.nextString()
      val expectedContent = "BadDataException: Can not parse url"

      // When
      val response = mockMvc.perform(
        MockMvcRequestBuilders.get("/evaluation")
          .queryParam("url", url)
      )

      // Then
      response.andExpect {
        status().`is`(400)
        content().string(expectedContent)
      }
    }

    @Test
    fun `should return 400 when urls list has not allowed protocol`() {
      // Given
      val urls = listOf(
        "http://mock1",
        "ftp://mock2"
      )
      val expectedContent = "BadDataException: Only 'http, https' urls are allowed"

      // When
      val response = mockMvc.perform(
        MockMvcRequestBuilders.get("/evaluation")
          .queryParam("url", *urls.toTypedArray())
      )

      // Then
      response.andExpect {
        status().`is`(400)
        content().string(expectedContent)
      }
    }

    @Test
    fun `should return 400 when exception happens while downloading`() {
      // Given
      val stringUrl = "http://mock1"
      val url = URL(stringUrl)
      val exception = BadDataException(random.nextString())
      val expectedContent = "BadDataException: ${exception.message}"

      every { politicalStatisticsDownloader.download(any()) } returns CompletableFuture.failedFuture(exception)

      // When
      val response = mockMvc.perform(
        MockMvcRequestBuilders.get("/evaluation")
          .queryParam("url", stringUrl)
      )

      // Then
      response.andExpect {
        status().`is`(400)
        content().string(expectedContent)
      }

      verify {
        politicalStatisticsDownloader.download(url)
      }
    }
  }
}

