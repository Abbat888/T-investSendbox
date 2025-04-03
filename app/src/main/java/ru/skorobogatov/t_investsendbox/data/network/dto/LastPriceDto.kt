package ru.skorobogatov.t_investsendbox.data.network.dto

import com.google.gson.annotations.SerializedName

data class LastPriceDto(
    @SerializedName("figi") val figi: String,
    @SerializedName("price") val price: PriceDto
)
