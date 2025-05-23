package ru.skorobogatov.t_investsendbox.data.mapper

import android.icu.util.Calendar
import ru.skorobogatov.t_investsendbox.data.network.dto.InstrumentDto
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument
import java.text.SimpleDateFormat
import java.util.Locale



fun InstrumentDto.toEntity(): Instrument = Instrument(
    figi = figi,
    name = name,
    ticker = ticker,
    brandUrl = brand.logoName.toBrandUrl(),
    currency = currency,
    instrumentKind = instrumentKind,
    first1minCandleDate = first1minCandleDate.toCalendar(),
    first1dayCandleDate = first1dayCandleDate.toCalendar()
)

private fun String.toBrandUrl(): String {
    val logoName = this.removeSuffix(".png")
    return "https://invest-brands.cdn-tinkoff.ru/${logoName}x320.png"
}

fun String.toCalendar(): Calendar{
    val date = Calendar.getInstance()
    if (this.length > 20){
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.UK)
        val time = simpleDateFormat.parse(this)?.time
        date.timeInMillis = time ?: date.timeInMillis
    } else {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK)
        val time = simpleDateFormat.parse(this)?.time
        date.timeInMillis = time ?: date.timeInMillis
    }
    return date
}

fun Calendar.toStringFormat(): String{
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK)
    return simpleDateFormat.format(this.time)
}