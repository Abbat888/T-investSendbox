package ru.skorobogatov.t_investsendbox.data.network.dto

import com.google.gson.annotations.SerializedName

data class CandlesShellDto(
    @SerializedName("candles") val candles: List<CandleDto>
)
