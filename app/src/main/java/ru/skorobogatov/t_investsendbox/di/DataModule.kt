package ru.skorobogatov.t_investsendbox.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.skorobogatov.t_investsendbox.data.local.db.FavouriteDatabase
import ru.skorobogatov.t_investsendbox.data.local.db.FavouriteInstrumentsDao
import ru.skorobogatov.t_investsendbox.data.network.api.ApiFactory
import ru.skorobogatov.t_investsendbox.data.network.api.ApiService
import ru.skorobogatov.t_investsendbox.data.repository.FavouriteRepositoryImpl
import ru.skorobogatov.t_investsendbox.data.repository.PriceRepositoryImpl
import ru.skorobogatov.t_investsendbox.data.repository.SearchRepositoryImpl
import ru.skorobogatov.t_investsendbox.domain.repository.FavouriteRepository
import ru.skorobogatov.t_investsendbox.domain.repository.PriceRepository
import ru.skorobogatov.t_investsendbox.domain.repository.SearchRepository

@Module
interface DataModule {

    @[ApplicationScope Binds]
    fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository

    @[ApplicationScope Binds]
    fun bindPriceRepository(impl: PriceRepositoryImpl): PriceRepository

    @[ApplicationScope Binds]
    fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    companion object{

        @[ApplicationScope Provides]
        fun provideApiService(): ApiService = ApiFactory.apiService

        @[ApplicationScope Provides]
        fun provideFavouriteDatabase(context: Context): FavouriteDatabase{
            return FavouriteDatabase.getInstance(context)
        }

        @[ApplicationScope Provides]
        fun provideFavouriteInstrumentsDao(database: FavouriteDatabase): FavouriteInstrumentsDao{
            return database.favouriteInstrumentsDao()
        }
    }
}