package ru.skorobogatov.t_investsendbox.data.repository

import okhttp3.MediaType
import okhttp3.RequestBody
import ru.skorobogatov.t_investsendbox.data.network.api.ApiService
import ru.skorobogatov.t_investsendbox.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SearchRepository {

    override suspend fun search(query: String): List<String> {
        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(
            mediaType,
            "{\"query\":\"$query\",\"instrumentKind\":\"INSTRUMENT_TYPE_UNSPECIFIED\",\"apiTradeAvailableFlag\":true}"
        )
        return apiService.searchInstrument(body).instruments.map { it.figi }
    }
}