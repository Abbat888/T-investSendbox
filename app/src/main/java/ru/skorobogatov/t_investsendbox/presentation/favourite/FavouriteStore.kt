package ru.skorobogatov.t_investsendbox.presentation.favourite

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument
import ru.skorobogatov.t_investsendbox.domain.entity.LastPrice
import ru.skorobogatov.t_investsendbox.domain.usecase.GetFavouriteInstrumentUseCase
import ru.skorobogatov.t_investsendbox.domain.usecase.GetLastPriceUseCase
import ru.skorobogatov.t_investsendbox.presentation.favourite.FavouriteStore.Intent
import ru.skorobogatov.t_investsendbox.presentation.favourite.FavouriteStore.Label
import ru.skorobogatov.t_investsendbox.presentation.favourite.FavouriteStore.State
import javax.inject.Inject

interface FavouriteStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickSearch : Intent

        data class ClickInstrumentItem(
            val instrument: Instrument
        ) : Intent
    }

    data class State(
        val instrumentItems: List<InstrumentItem>
    ) {

        data class InstrumentItem(
            val instrument: Instrument,
            val lastPriceState: LastPriceState
        )

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

        data object ClickSearch : Label

        data class ClickInstrumentItem(
            val instrument: Instrument
        ) : Label
    }
}

class FavouriteStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getFavouriteInstrumentUseCase: GetFavouriteInstrumentUseCase,
    private val getLastPriceUseCase: GetLastPriceUseCase
) {

    fun create(): FavouriteStore =
        object : FavouriteStore, Store<Intent, State, Label> by storeFactory.create(
            name = "FavouriteStore",
            initialState = State(listOf()),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class FavouriteInstrumentsLoaded(
            val instruments: List<Instrument>
        ) : Action
    }

    private sealed interface Msg {

        data class FavouriteInstrumentsLoaded(
            val instruments: List<Instrument>
        ) : Msg

        data class LastPriceLoaded(
            val figi: String,
            val price: Float
        ) : Msg

        data class LastPriceLoadingError(
            val figi: String
        ) : Msg

        data object LastPricesLoadingError : Msg

        data object LastPricesIsLoading : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {

        override fun invoke() {
            scope.launch {
                getFavouriteInstrumentUseCase().collect {
                    dispatch(Action.FavouriteInstrumentsLoaded(it))
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.ClickInstrumentItem -> {
                    publish(Label.ClickInstrumentItem(intent.instrument))
                }

                Intent.ClickSearch -> {
                    publish(Label.ClickSearch)
                }
            }
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.FavouriteInstrumentsLoaded -> {
                    val instruments = action.instruments
                    dispatch(Msg.FavouriteInstrumentsLoaded(instruments))
                    scope.launch {
                        loadLastPrices(instruments)
                    }
                }
            }
        }

        private suspend fun loadLastPrices(instruments: List<Instrument>) {
            dispatch(Msg.LastPricesIsLoading)
            val figiList = instruments.map { it.figi }
            try {
                val lastPrices = getLastPriceUseCase(figiList)
                lastPrices.forEach {
                    if (figiList.contains(it.figi)) {
                        dispatch(Msg.LastPriceLoaded(it.figi, it.price))
                    } else {
                        dispatch(Msg.LastPriceLoadingError(it.figi))
                    }
                }
            } catch (e: Exception) {
                dispatch(Msg.LastPricesLoadingError)
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {

        override fun State.reduce(msg: Msg): State = when (msg) {

            is Msg.FavouriteInstrumentsLoaded -> {
                copy(
                    instrumentItems = msg.instruments.map {
                        State.InstrumentItem(
                            instrument = it,
                            lastPriceState = State.LastPriceState.Initial
                        )
                    }
                )
            }

            is Msg.LastPriceLoaded -> {
                copy(
                    instrumentItems = instrumentItems.map {
                        if (it.instrument.figi == msg.figi) {
                            it.copy(
                                lastPriceState = State.LastPriceState.Loaded(
                                    lastPrice = LastPrice(figi = msg.figi, price = msg.price)
                                )
                            )
                        } else {
                            it
                        }
                    }
                )
            }

            is Msg.LastPriceLoadingError -> {
                copy(
                    instrumentItems = instrumentItems.map {
                        if (it.instrument.figi == msg.figi) {
                            it.copy(lastPriceState = State.LastPriceState.Error)
                        } else {
                            it
                        }
                    }
                )
            }

            Msg.LastPricesIsLoading -> {
                copy(
                    instrumentItems = instrumentItems.map {
                        State.InstrumentItem(
                            instrument = it.instrument,
                            lastPriceState = State.LastPriceState.Loading
                        )
                    }
                )
            }

            Msg.LastPricesLoadingError -> {
                copy(
                    instrumentItems = instrumentItems.map{
                        it.copy(lastPriceState = State.LastPriceState.Error)
                    }
                )
            }
        }
    }
}
