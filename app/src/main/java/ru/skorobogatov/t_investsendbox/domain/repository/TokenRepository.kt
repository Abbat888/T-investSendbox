package ru.skorobogatov.t_investsendbox.domain.repository

interface TokenRepository {

    suspend fun saveToken(token: String)

    suspend fun checkToken(): Boolean
}