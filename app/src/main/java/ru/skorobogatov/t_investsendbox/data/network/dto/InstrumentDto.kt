package ru.skorobogatov.t_investsendbox.data.network.dto

import com.google.gson.annotations.SerializedName

data class InstrumentDto(
    @SerializedName("figi") val figi: String,
    @SerializedName("name") val name: String,
    @SerializedName("ticker") val ticker: String,
    @SerializedName("brand") val brand: BrandDto,
    @SerializedName("currency") val currency: String,
    @SerializedName("instrumentKind") val instrumentKind: String,
    @SerializedName("first1minCandleDate") val first1minCandleDate: String,
    @SerializedName("first1dayCandleDate") val first1dayCandleDate: String
)
