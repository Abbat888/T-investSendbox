package ru.skorobogatov.t_investsendbox.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_instruments")
data class InstrumentDbModel(
    @PrimaryKey val figi: String,
    val name: String,
    val ticker: String,
    val brandUrl: String,
    val currency: String,
    val instrumentKind: String,
    val first1minCandleDate: Long,
    val first1dayCandleDate: Long
)
