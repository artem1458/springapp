package com.artem1458.recruitmentexercise.utils

/*
* input: Map("a": 1, "b": 1, "c": 2)
* output: Map(1: ["a", "b"], 2: ["c"])
*  */
fun <K, V> Map<K, V>.invert(): Map<V, List<K>> = toList().groupBy({ it.second }, { it.first })
