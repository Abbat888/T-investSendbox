package ru.skorobogatov.t_investsendbox.presentation.search

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument
import ru.skorobogatov.t_investsendbox.domain.entity.LastPrice
import ru.skorobogatov.t_investsendbox.domain.usecase.GetInstrumentInfoUseCase
import ru.skorobogatov.t_investsendbox.domain.usecase.GetLastPriceUseCase
import ru.skorobogatov.t_investsendbox.domain.usecase.SearchInstrumentUseCase
import ru.skorobogatov.t_investsendbox.presentation.search.SearchStore.Intent
import ru.skorobogatov.t_investsendbox.presentation.search.SearchStore.Label
import ru.skorobogatov.t_investsendbox.presentation.search.SearchStore.State
import ru.skorobogatov.t_investsendbox.presentation.search.SearchStore.State.SearchState.*
import javax.inject.Inject

interface SearchStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data class ChangeSearchQuery(
            val query: String
        ) : Intent

        data object ClickBack : Intent

        data object ClickSearch : Intent

        data class ClickInstrument(val instrument: Instrument) : Intent
    }

    data class State(
        val searchQuery: String,
        val searchState: SearchState,
        val instrumentItems: List<InstrumentItem>
    ) {

        sealed interface SearchState {

            data object Initial : SearchState

            data object Loading : SearchState

            data object Error : SearchState

            data object EmptyResult : SearchState

            data class SuccessLoaded(
                val figiList: List<String>
            ) : SearchState
        }

        data class InstrumentItem(
            val instrumentState: InstrumentState,
            val lastPriceState: LastPriceState
        )

        sealed interface InstrumentState {

            data object Initial : InstrumentState

            data object Loading : InstrumentState

            data object Error : InstrumentState

            data class Loaded(
                val instrument: Instrument
            ) : InstrumentState
        }

        sealed interface LastPriceState {

            data object Initial : LastPriceState

            data object Loading : LastPriceState

            data object Error : LastPriceState

            data class Loaded(
                val lastPrice: LastPrice
            ) : LastPriceState
        }
    }

    sealed interface Label {

        data object ClickBack : Label

        data class ClickInstrumentItem(
            val instrument: Instrument
        ) : Label
    }
}

class SearchStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getInstrumentInfoUseCase: GetInstrumentInfoUseCase,
    private val getLastPriceUseCase: GetLastPriceUseCase,
    private val searchInstrumentUseCase: SearchInstrumentUseCase
) {

    fun create(): SearchStore =
        object : SearchStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SearchStore",
            initialState = State(
                searchQuery = "",
                searchState = State.SearchState.Initial,
                instrumentItems = listOf()
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
    }

    private sealed interface Msg {

        data class ChangeSearchQuery(
            val query: String
        ) : Msg

        data object LoadingSearchResult : Msg

        data object SearchResultError : Msg

        data class SearchResultLoaded(
            val figiList: List<String>
        ) : Msg

        data object LoadingInstrumentItems : Msg

        data object InstrumentItemsError : Msg

        data class InstrumentItemsLoaded(
            val instrumentItems: List<State.InstrumentItem>
        ) : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        private var searchJob: Job? = null

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.ChangeSearchQuery -> {
                    dispatch(Msg.ChangeSearchQuery(intent.query))
                }

                Intent.ClickBack -> {
                    publish(Label.ClickBack)
                }

                is Intent.ClickInstrument -> {
                    publish(Label.ClickInstrumentItem(intent.instrument))
                }

                Intent.ClickSearch -> {
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        dispatch(Msg.LoadingSearchResult)
                        try {
                            val figiList = searchInstrumentUseCase(state().searchQuery)
                            dispatch(Msg.SearchResultLoaded(figiList))
                            dispatch(Msg.LoadingInstrumentItems)
                            try {
                                val instrumentItems = figiList.map {
                                    State.InstrumentItem(
                                        instrumentState = try {
                                            State.InstrumentState.Loaded(loadInstrument(it))
                                        } catch (e: Exception) {
                                            State.InstrumentState.Error
                                        },
                                        lastPriceState = try {
                                            State.LastPriceState.Loaded(loadLastPrice(it))
                                        } catch (e: Exception) {
                                            State.LastPriceState.Error
                                        }
                                    )
                                }
                                dispatch(Msg.InstrumentItemsLoaded(instrumentItems))
                            } catch (e: Exception) {
                                dispatch(Msg.InstrumentItemsError)
                            }


                        } catch (e: Exception) {
                            dispatch(Msg.SearchResultError)
                        }
                    }
                }
            }
        }

        private suspend fun loadInstrument(figi: String): Instrument {
            return getInstrumentInfoUseCase(figi)
        }

        private suspend fun loadLastPrice(figi: String): LastPrice {
            return getLastPriceUseCase(listOf(figi)).first()
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {

        override fun State.reduce(msg: Msg): State = when (msg) {

            is Msg.ChangeSearchQuery -> {
                copy(searchQuery = msg.query)
            }

            Msg.LoadingSearchResult -> {
                copy(searchState = Loading)
            }

            Msg.SearchResultError -> {
                copy(searchState = Error)
            }

            is Msg.SearchResultLoaded -> {
                if (msg.figiList.isEmpty()) {
                    copy(searchState = EmptyResult)
                } else {
                    copy(searchState = SuccessLoaded(msg.figiList))
                }
            }

            Msg.InstrumentItemsError -> {
                copy(instrumentItems = instrumentItems.map {
                    it.copy(
                        instrumentState = State.InstrumentState.Error,
                        lastPriceState = State.LastPriceState.Error
                    )
                })
            }

            is Msg.InstrumentItemsLoaded -> {
                copy(instrumentItems = msg.instrumentItems)
            }

            Msg.LoadingInstrumentItems -> {
                copy(instrumentItems = instrumentItems.map {
                    it.copy(
                        instrumentState = State.InstrumentState.Loading,
                        lastPriceState = State.LastPriceState.Loading
                    )
                })
            }
        }
    }
}
