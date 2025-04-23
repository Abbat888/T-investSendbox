package ru.skorobogatov.t_investsendbox.domain.usecase

import ru.skorobogatov.t_investsendbox.domain.repository.TokenRepository
import javax.inject.Inject

class RegistrationTokenUseCase @Inject constructor(
    private val repository: TokenRepository
) {

    suspend fun saveToken(token: String) = repository.saveToken(token)
}