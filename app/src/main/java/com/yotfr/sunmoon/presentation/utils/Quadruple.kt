package com.yotfr.sunmoon.presentation.utils

// used to combine 4 flows and get a single result
data class Quadruple<T1, T2, T3, T4>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4
)
