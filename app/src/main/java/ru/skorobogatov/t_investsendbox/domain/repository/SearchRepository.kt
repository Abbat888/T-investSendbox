package ru.skorobogatov.t_investsendbox.domain.repository

import ru.skorobogatov.t_investsendbox.domain.entity.Instrument

interface SearchRepository {

    suspend fun search(query: String): List<String>
}