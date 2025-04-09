package ru.skorobogatov.t_investsendbox.domain.entity

import android.icu.util.Calendar
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Instrument(
    val figi: String,
    val name: String,
    val ticker: String,
    val brandUrl: String,
    val currency: String,
    val instrumentKind: String,
    val first1minCandleDate: Calendar,
    val first1dayCandleDate: Calendar
): Parcelable