package ru.skorobogatov.t_investsendbox.data.mapper

import ru.skorobogatov.t_investsendbox.data.network.dto.CandleDto
import ru.skorobogatov.t_investsendbox.domain.entity.Candle

fun CandleDto.toEntity(): Candle = Candle(
    open = open.toFloat(),
    close = close.toFloat(),
    high = high.toFloat(),
    low = low.toFloat(),
    volume = volume.toInt(),
    time = time.toCalendar(),
    isComplete = isComplete
)

fun List<CandleDto>.toEntities(): List<Candle> = map { it.toEntity() }