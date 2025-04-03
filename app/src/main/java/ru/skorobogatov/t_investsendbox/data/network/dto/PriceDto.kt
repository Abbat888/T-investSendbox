package ru.skorobogatov.t_investsendbox.data.network.dto

import com.google.gson.annotations.SerializedName

data class PriceDto(
    @SerializedName("nano") val nano: Int,
    @SerializedName("units") val units: String
)
