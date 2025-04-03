package ru.skorobogatov.t_investsendbox.domain.usecase

import ru.skorobogatov.t_investsendbox.domain.repository.PriceRepository
import javax.inject.Inject

class GetLastPriceUseCase @Inject constructor(
    private val repository: PriceRepository
) {

    suspend operator fun invoke(figiList: List<String>) = repository.getLastPrice(figiList)
}