package ru.skorobogatov.t_investsendbox.domain.usecase

import ru.skorobogatov.t_investsendbox.domain.repository.GetInfoRepository
import javax.inject.Inject

class GetInfoUseCase @Inject constructor(
    val repository: GetInfoRepository
) {

    suspend operator fun invoke() = repository.getInfo()
}