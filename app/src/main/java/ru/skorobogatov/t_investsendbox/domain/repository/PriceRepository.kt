package ru.skorobogatov.t_investsendbox.domain.repository

import android.icu.util.Calendar
import ru.skorobogatov.t_investsendbox.domain.entity.Candle
import ru.skorobogatov.t_investsendbox.domain.entity.LastPrice

interface PriceRepository {

    suspend fun getLastPrice(figiList: List<String>): List<LastPrice>

    suspend fun getCandles(
        figi: String,
        from: Calendar,
        to: Calendar,
        interval: String,
        limit: Int
    ): List<Candle>
}