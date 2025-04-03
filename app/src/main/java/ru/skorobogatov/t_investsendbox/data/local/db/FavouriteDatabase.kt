package ru.skorobogatov.t_investsendbox.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.skorobogatov.t_investsendbox.data.local.model.InstrumentDbModel

@Database(
    entities = [InstrumentDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class FavouriteDatabase : RoomDatabase() {

    abstract fun favouriteInstrumentsDao(): FavouriteInstrumentsDao

    companion object {

        private const val DB_NAME = "FavouriteDatabase"
        private var INSTANCE: FavouriteDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): FavouriteDatabase {

            INSTANCE?.let { return it }

            synchronized(LOCK) {
                INSTANCE?.let { return it }

                val database = Room.databaseBuilder(
                    context = context,
                    klass = FavouriteDatabase::class.java,
                    name = DB_NAME
                ).build()

                INSTANCE = database
                return database
            }
        }
    }
}