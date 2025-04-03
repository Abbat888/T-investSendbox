package ru.skorobogatov.t_investsendbox.data.network.dto

import com.google.gson.annotations.SerializedName

data class BrandDto(
    @SerializedName("logoName") val logoName: String
)
