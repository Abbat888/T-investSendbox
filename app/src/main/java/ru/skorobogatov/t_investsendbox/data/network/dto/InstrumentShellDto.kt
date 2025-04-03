package ru.skorobogatov.t_investsendbox.data.network.dto

import com.google.gson.annotations.SerializedName

data class InstrumentShellDto(
    @SerializedName("instrument") val instrument: InstrumentDto
)
