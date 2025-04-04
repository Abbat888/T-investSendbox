package ru.skorobogatov.t_investsendbox.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.skorobogatov.t_investsendbox.data.local.db.FavouriteInstrumentsDao
import ru.skorobogatov.t_investsendbox.data.mapper.toDbModel
import ru.skorobogatov.t_investsendbox.data.mapper.toEntities
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument
import ru.skorobogatov.t_investsendbox.domain.repository.FavouriteRepository
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
    private val favouriteInstrumentsDao: FavouriteInstrumentsDao
) : FavouriteRepository {

    override val favouriteInstrument: Flow<List<Instrument>> =
        favouriteInstrumentsDao.getFavouriteInstruments()
            .map { it.toEntities() }

    override fun observeIsFavourite(figi: String): Flow<Boolean> =
        favouriteInstrumentsDao.observeIsFavourite(figi)

    override suspend fun addToFavourite(instrument: Instrument) {
        favouriteInstrumentsDao.addToFavourite(instrument.toDbModel())
    }

    override suspend fun removeFromFavourite(figi: String) {
        favouriteInstrumentsDao.removeFromFavourite(figi)
    }
}