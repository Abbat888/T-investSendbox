package ru.skorobogatov.t_investsendbox.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.skorobogatov.t_investsendbox.data.local.model.InstrumentDbModel

@Dao
interface FavouriteInstrumentsDao {

    @Query("SELECT * FROM favourite_instruments")
    fun getFavouriteInstruments(): Flow<List<InstrumentDbModel>>

    @Query("SELECT EXISTS (SELECT * FROM favourite_instruments WHERE figi=:figi LIMIT 1)")
    fun observeIsFavourite(figi: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavourite(instrumentDbModel: InstrumentDbModel)

    @Query("DELETE FROM favourite_instruments WHERE figi=:figi")
    suspend fun removeFromFavourite(figi: String)
}