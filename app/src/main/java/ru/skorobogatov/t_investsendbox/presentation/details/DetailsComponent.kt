package ru.skorobogatov.t_investsendbox.presentation.details

import kotlinx.coroutines.flow.StateFlow
import ru.skorobogatov.t_investsendbox.domain.entity.Timeframe

interface DetailsComponent {

    val model: StateFlow<DetailsStore.State>

    fun onClickBack()

    fun onClickChangeFavouriteStatus()

    fun onClickChangeTimeframe()

    fun onClickChangePeriod()

    fun onTimeFrameChanged(
        timeframe: Timeframe
    )

    fun onPeriodChanged(
        from: Long,
        to: Long
    )
}