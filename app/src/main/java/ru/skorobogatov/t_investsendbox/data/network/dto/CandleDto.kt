package ru.skorobogatov.t_investsendbox.data.network.dto

import com.google.gson.annotations.SerializedName

data class CandleDto(
    @SerializedName("open") val open: PriceDto,
    @SerializedName("close") val close: PriceDto,
    @SerializedName("high") val high: PriceDto,
    @SerializedName("low") val low: PriceDto,
    @SerializedName("volume") val volume: String,
    @SerializedName("time") val time: String,
    @SerializedName("isComplete") val isComplete: Boolean
)
