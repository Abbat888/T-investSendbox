package ru.skorobogatov.t_investsendbox.data.network.dto

import com.google.gson.annotations.SerializedName

data class InfoDto(
    @SerializedName("tariff") val tariff: String? = null
)
