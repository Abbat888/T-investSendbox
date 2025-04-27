package ru.skorobogatov.t_investsendbox.data.network.api

import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.skorobogatov.t_investsendbox.data.network.dto.CandlesShellDto
import ru.skorobogatov.t_investsendbox.data.network.dto.InfoDto
import ru.skorobogatov.t_investsendbox.data.network.dto.InstrumentShellDto
import ru.skorobogatov.t_investsendbox.data.network.dto.LastPricesShellDto
import ru.skorobogatov.t_investsendbox.data.network.dto.SearchedInstrumentsShellDto

interface ApiService {

    @POST("tinkoff.public.invest.api.contract.v1.InstrumentsService/GetInstrumentBy")
    suspend fun loadInstrumentInfo(
        @Body body: RequestBody
    ): InstrumentShellDto

    @POST("tinkoff.public.invest.api.contract.v1.MarketDataService/GetLastPrices")
    suspend fun loadLastPrice(
        @Body body: RequestBody
    ): LastPricesShellDto

    @POST("tinkoff.public.invest.api.contract.v1.InstrumentsService/FindInstrument")
    suspend fun searchInstrument(
        @Body body: RequestBody
    ): SearchedInstrumentsShellDto

    @POST("tinkoff.public.invest.api.contract.v1.MarketDataService/GetCandles")
    suspend fun getCandles(
        @Body body: RequestBody
    ): CandlesShellDto

    @POST("tinkoff.public.invest.api.contract.v1.UsersService/GetInfo")
    suspend fun getInfo(
        @Body body: RequestBody = RequestBody.create(MediaType.parse("application/json"), "{}")
    ): Response<InfoDto>
}