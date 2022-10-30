package com.artem1458.recruitmentexercise.utils

import java.io.Closeable

/* It's a Kotlin extension function that closes a Closeable object without throwing an exception. */
fun Closeable.closeQuietly() {
  runCatching {
    close()
  }
}
