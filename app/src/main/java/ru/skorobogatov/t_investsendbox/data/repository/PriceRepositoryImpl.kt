package ru.skorobogatov.t_investsendbox.data.repository

import android.icu.util.Calendar
import okhttp3.MediaType
import okhttp3.RequestBody
import ru.skorobogatov.t_investsendbox.data.mapper.toEntities
import ru.skorobogatov.t_investsendbox.data.mapper.toEntity
import ru.skorobogatov.t_investsendbox.data.mapper.toStringFormat
import ru.skorobogatov.t_investsendbox.data.network.api.ApiService
import ru.skorobogatov.t_investsendbox.domain.entity.Candle
import ru.skorobogatov.t_investsendbox.domain.entity.LastPrice
import ru.skorobogatov.t_investsendbox.domain.repository.PriceRepository
import javax.inject.Inject

class PriceRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : PriceRepository {

    override suspend fun getLastPrice(figiList: List<String>): List<LastPrice> {
        val mediaType = MediaType.parse("application/json")
        val instrumentIds = StringBuilder().also {
            figiList.forEachIndexed { index, figi ->
                if (index == 0) {
                    it.append("\"$figi\",")
                } else if (index == figiList.lastIndex) {
                    it.appendLine("\"$figi\"")
                } else {
                    it.appendLine("\"$figi\",")
                }
            }
        }
        val body = RequestBody.create(
            mediaType,
            "{\n  \"instrumentId\": [\n    $instrumentIds\n  ],\n  \"lastPriceType\": \"LAST_PRICE_UNSPECIFIED\",\n  \"instrumentStatus\": \"INSTRUMENT_STATUS_UNSPECIFIED\"\n}"
        )
        return apiService.loadLastPrice(body).lastPrices.toEntities()
    }

    override suspend fun getCandles(
        figi: String,
        from: Calendar,
        to: Calendar,
        interval: String,
        limit: Int
    ): List<Candle> {
        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(
            mediaType,
            "{\"from\":\"${from.toStringFormat()}\",\"to\":\"${to.toStringFormat()}\",\"interval\":\"$interval\",\"instrumentId\":\"$figi\",\"candleSourceType\":\"CANDLE_SOURCE_UNSPECIFIED\",\"limit\":$limit}"
        )
        return apiService.getCandles(body).candles.toEntities()
    }
}