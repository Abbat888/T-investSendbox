package ru.skorobogatov.t_investsendbox.presentation.favourite

import kotlinx.coroutines.flow.StateFlow
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument

interface FavouriteComponent {

    val model: StateFlow<FavouriteStore.State>

    fun onClickSearch()

    fun onClickInstrumentItem(instrument: Instrument)
}