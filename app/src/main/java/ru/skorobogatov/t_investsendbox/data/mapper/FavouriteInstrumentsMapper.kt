package ru.skorobogatov.t_investsendbox.data.mapper

import android.icu.util.Calendar
import ru.skorobogatov.t_investsendbox.data.local.model.InstrumentDbModel
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument

fun Instrument.toDbModel(): InstrumentDbModel = InstrumentDbModel(
    figi = figi,
    name = name,
    ticker = ticker,
    brandUrl = brandUrl,
    currency = currency,
    instrumentKind = instrumentKind,
    first1minCandleDate = first1minCandleDate.timeInMillis,
    first1dayCandleDate = first1dayCandleDate.timeInMillis
)

fun InstrumentDbModel.toEntity(): Instrument = Instrument(
    figi = figi,
    name = name,
    ticker = ticker,
    brandUrl = brandUrl,
    currency = currency,
    instrumentKind = instrumentKind,
    first1minCandleDate = first1minCandleDate.toCalendar(),
    first1dayCandleDate = first1dayCandleDate.toCalendar()
)

fun List<InstrumentDbModel>.toEntities(): List<Instrument> = this.map { it.toEntity() }

private fun Long.toCalendar(): Calendar = Calendar.getInstance().also { it.timeInMillis = this }