package ru.skorobogatov.t_investsendbox.data.mapper

import ru.skorobogatov.t_investsendbox.data.network.dto.LastPriceDto
import ru.skorobogatov.t_investsendbox.data.network.dto.PriceDto
import ru.skorobogatov.t_investsendbox.domain.entity.LastPrice

fun LastPriceDto.toEntity(): LastPrice = LastPrice(this.figi, this.price.toFloat())

fun List<LastPriceDto>.toEntities(): List<LastPrice> = map { it.toEntity() }

fun PriceDto.toFloat(): Float {
    return this.units.toFloat() + (this.nano / 1_000_000_000)
}