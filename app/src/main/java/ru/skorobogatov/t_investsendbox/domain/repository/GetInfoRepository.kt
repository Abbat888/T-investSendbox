package ru.skorobogatov.t_investsendbox.domain.repository

interface GetInfoRepository {

    suspend fun getInfo(): Int
}