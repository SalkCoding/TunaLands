package com.salkcoding.tunalands.util

import kotlin.math.pow

fun Double.round(roundPlace: Int): Double {
    val p = 10.0.pow(roundPlace)
    return kotlin.math.round(this * p) / p
}