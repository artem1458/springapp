package com.artem1458.recruitmentexercise.utils

import java.util.concurrent.Future

fun <T> List<Future<T>>.collect(): List<T> = map { it.get() }
