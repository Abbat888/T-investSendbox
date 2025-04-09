package ru.skorobogatov.t_investsendbox.presentation.search

import kotlinx.coroutines.flow.StateFlow
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument

interface SearchComponent {

    val model: StateFlow<SearchStore.State>

    fun changeSearchQuery(query: String)

    fun onClickBack()

    fun onClickSearch()

    fun onClickInstrument(instrument: Instrument)
}