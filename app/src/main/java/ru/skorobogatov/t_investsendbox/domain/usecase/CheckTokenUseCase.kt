package ru.skorobogatov.t_investsendbox.domain.usecase

import ru.skorobogatov.t_investsendbox.domain.repository.TokenRepository
import javax.inject.Inject

class CheckTokenUseCase @Inject constructor(
    private val repository: TokenRepository
) {

    suspend operator fun invoke(): Boolean = repository.checkToken()
}