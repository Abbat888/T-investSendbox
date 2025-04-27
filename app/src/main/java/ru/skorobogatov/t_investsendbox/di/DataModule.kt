package ru.skorobogatov.t_investsendbox.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.skorobogatov.t_investsendbox.data.local.db.FavouriteDatabase
import ru.skorobogatov.t_investsendbox.data.local.db.FavouriteInstrumentsDao
import ru.skorobogatov.t_investsendbox.data.network.api.ApiService
import ru.skorobogatov.t_investsendbox.data.repository.FavouriteRepositoryImpl
import ru.skorobogatov.t_investsendbox.data.repository.GetInfoRepositoryImpl
import ru.skorobogatov.t_investsendbox.data.repository.PriceRepositoryImpl
import ru.skorobogatov.t_investsendbox.data.repository.SearchRepositoryImpl
import ru.skorobogatov.t_investsendbox.data.repository.TokenRepositoryImpl
import ru.skorobogatov.t_investsendbox.data.settings.TokenInterface
import ru.skorobogatov.t_investsendbox.data.settings.TokenManager
import ru.skorobogatov.t_investsendbox.domain.repository.FavouriteRepository
import ru.skorobogatov.t_investsendbox.domain.repository.GetInfoRepository
import ru.skorobogatov.t_investsendbox.domain.repository.PriceRepository
import ru.skorobogatov.t_investsendbox.domain.repository.SearchRepository
import ru.skorobogatov.t_investsendbox.domain.repository.TokenRepository

@Module
interface DataModule {

    @[ApplicationScope Binds]
    fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository

    @[ApplicationScope Binds]
    fun bindPriceRepository(impl: PriceRepositoryImpl): PriceRepository

    @[ApplicationScope Binds]
    fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @[ApplicationScope Binds]
    fun bindTokenRepository(impl: TokenRepositoryImpl): TokenRepository

    @[ApplicationScope Binds]
    fun bindGetInfoRepository(impl: GetInfoRepositoryImpl): GetInfoRepository

    companion object {

        const val BASE_URL = "https://sandbox-invest-public-api.tinkoff.ru/rest/"

        @[ApplicationScope Provides]
        fun provideApiService(tokenInterface: TokenInterface): ApiService {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val body = originalRequest.body()
                    val newRequest = originalRequest.newBuilder()
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Bearer ${tokenInterface.getToken()}")
                        .build()
                    chain.proceed(newRequest)
                }.build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            return retrofit.create()
        }

        @[ApplicationScope Provides]
        fun provideFavouriteDatabase(context: Context): FavouriteDatabase {
            return FavouriteDatabase.getInstance(context)
        }

        @[ApplicationScope Provides]
        fun provideFavouriteInstrumentsDao(database: FavouriteDatabase): FavouriteInstrumentsDao {
            return database.favouriteInstrumentsDao()
        }

        @[ApplicationScope Provides]
        fun provideTokenManager(context: Context): TokenInterface {
            return TokenManager.getInstance(context)
        }
    }
}