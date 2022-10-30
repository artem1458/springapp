package com.artem1458.recruitmentexercise.testutils

import org.jeasy.random.EasyRandom

abstract class BaseTest {

  protected companion object {
    val random = EasyRandom()

    inline fun <reified T> EasyRandom.build(): T = nextObject(T::class.java)
    inline fun <reified T> EasyRandom.buildList(count: Int): List<T> =
      if (count < 0)
        throw IndexOutOfBoundsException("Count should not be less than 0")
      else (0 until count).map { nextObject(T::class.java) }
    fun EasyRandom.nextString() = "string_${nextDouble()}"
  }
}
