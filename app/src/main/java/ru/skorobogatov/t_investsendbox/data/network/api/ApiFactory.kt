package ru.skorobogatov.t_investsendbox.data.network.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.skorobogatov.t_investsendbox.BuildConfig

object ApiFactory {

    private const val BASE_URL = "https://sandbox-invest-public-api.tinkoff.ru/rest/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val body = originalRequest.body()
            val newRequest = originalRequest.newBuilder()
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer ${BuildConfig.T_INVEST_SENDBOX_TOKEN}")
                .build()
            chain.proceed(newRequest)
        }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val apiService: ApiService = retrofit.create()
}