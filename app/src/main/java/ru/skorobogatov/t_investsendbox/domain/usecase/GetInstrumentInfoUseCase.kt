package ru.skorobogatov.t_investsendbox.domain.usecase

import ru.skorobogatov.t_investsendbox.domain.repository.SearchRepository
import javax.inject.Inject

class GetInstrumentInfoUseCase @Inject constructor(
    private val repository: SearchRepository
) {

    suspend operator fun invoke(figi: String) = repository.getInstrumentInfo(figi)
}