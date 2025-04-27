package ru.skorobogatov.t_investsendbox.data.repository

import ru.skorobogatov.t_investsendbox.data.network.api.ApiService
import ru.skorobogatov.t_investsendbox.domain.repository.GetInfoRepository
import javax.inject.Inject

class GetInfoRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : GetInfoRepository {

    override suspend fun getInfo(): Int {
        val response = apiService.getInfo()
        return response.code()
    }
}