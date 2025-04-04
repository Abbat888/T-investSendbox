package ru.skorobogatov.t_investsendbox.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument

interface FavouriteRepository {

    val favouriteInstrument: Flow<List<Instrument>>

    fun observeIsFavourite(figi: String): Flow<Boolean>

    suspend fun addToFavourite(instrument: Instrument)

    suspend fun removeFromFavourite(figi: String)
}