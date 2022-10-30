package com.artem1458.recruitmentexercise.utils

import java.util.concurrent.Future

/* An extension function that takes a list of futures and returns a list of the results of those futures. */
fun <T> List<Future<T>>.collect(): List<T> = map { it.get() }
