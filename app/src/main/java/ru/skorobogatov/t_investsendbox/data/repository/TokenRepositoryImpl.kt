package ru.skorobogatov.t_investsendbox.data.repository

import ru.skorobogatov.t_investsendbox.data.settings.TokenInterface
import ru.skorobogatov.t_investsendbox.domain.repository.TokenRepository
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val tokenInterface: TokenInterface
) : TokenRepository {

    override suspend fun saveToken(token: String) {
        tokenInterface.saveToken(token)
    }
}