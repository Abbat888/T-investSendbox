package ru.skorobogatov.t_investsendbox.data.network.dto

import com.google.gson.annotations.SerializedName

data class LastPricesShellDto(
    @SerializedName("lastPrices") val lastPrices: List<LastPriceDto>
)
