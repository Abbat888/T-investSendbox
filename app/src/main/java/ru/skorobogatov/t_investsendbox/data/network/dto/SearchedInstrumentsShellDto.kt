package ru.skorobogatov.t_investsendbox.data.network.dto

import com.google.gson.annotations.SerializedName

data class SearchedInstrumentsShellDto(
    @SerializedName("instruments") val instruments: List<SearchedInstrumentDto>
)
