package ru.skorobogatov.t_investsendbox.domain.usecase

import android.icu.util.Calendar
import ru.skorobogatov.t_investsendbox.domain.repository.PriceRepository
import javax.inject.Inject

class GetCandlesUseCase @Inject constructor(
    private val repository: PriceRepository
) {

    suspend operator fun invoke(
        figi: String,
        from: Calendar,
        to: Calendar,
        interval: String,
        limit: Int
    ) = repository.getCandles(figi, from, to, interval, limit)
}