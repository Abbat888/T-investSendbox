package ru.skorobogatov.t_investsendbox.domain.entity

import android.icu.util.Calendar

data class Candle(
    val open: Float,
    val close: Float,
    val high: Float,
    val low: Float,
    val volume: Int,
    val time: Calendar,
    val isComplete: Boolean
)
