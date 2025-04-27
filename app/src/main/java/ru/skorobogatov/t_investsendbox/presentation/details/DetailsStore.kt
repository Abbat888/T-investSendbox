package ru.skorobogatov.t_investsendbox.presentation.details

import android.icu.util.Calendar
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.skorobogatov.t_investsendbox.domain.entity.Candle
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument
import ru.skorobogatov.t_investsendbox.domain.entity.Timeframe
import ru.skorobogatov.t_investsendbox.domain.usecase.ChangeFavouriteStateUseCase
import ru.skorobogatov.t_investsendbox.domain.usecase.GetCandlesUseCase
import ru.skorobogatov.t_investsendbox.domain.usecase.ObserveFavouriteStateUseCase
import ru.skorobogatov.t_investsendbox.presentation.details.DetailsStore.Intent
import ru.skorobogatov.t_investsendbox.presentation.details.DetailsStore.Label
import ru.skorobogatov.t_investsendbox.presentation.details.DetailsStore.State
import javax.inject.Inject

interface DetailsStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickBack : Intent

        data object ClickChangeFavouriteStatus : Intent

        data object ClickChangeTimeframe : Intent

        data object ClickChangePeriod : Intent

        data class TimeframeChanged(
            val timeframe: Timeframe
        ) : Intent

        data class PeriodChanged(
            val from: Long,
            val to: Long
        ) : Intent
    }

    data class State(
        val instrument: Instrument,
        val isFavourite: Boolean,
        val candlesState: CandlesState,
        val timeframeState: TimeframeState,
        val periodState: PeriodState
    ) {

        sealed interface CandlesState {

            data object Initial : CandlesState

            data object Loading : CandlesState

            data object Error : CandlesState

            data class Loaded(
                val candlesList: List<Candle>
            ) : CandlesState
        }

        sealed interface TimeframeState {

            data object OnChange : TimeframeState

            data class SelectedTimeframe(
                val timeframe: Timeframe
            ) : TimeframeState
        }

        sealed interface PeriodState {

            data object OnChange : PeriodState

            data class SelectedPeriod(
                val from: Long,
                val to: Long
            ) : PeriodState
        }
    }

    sealed interface Label {

        data object ClickBack : Label
    }
}

class DetailsStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getCandlesUseCase: GetCandlesUseCase,
    private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase,
    private val observeFavouriteStateUseCase: ObserveFavouriteStateUseCase
) {

    fun create(instrument: Instrument): DetailsStore =
        object : DetailsStore, Store<Intent, State, Label> by storeFactory.create(
            name = "DetailsStore",
            initialState = State(
                instrument = instrument,
                isFavourite = false,
                candlesState = State.CandlesState.Initial,
                timeframeState = State.TimeframeState.SelectedTimeframe(Timeframe.CANDLE_INTERVAL_30_MIN),
                periodState = State.PeriodState.SelectedPeriod(
                    from = Calendar.getInstance().timeInMillis - (86400000 * 3),
                    to = Calendar.getInstance().timeInMillis - 86400000
                )
            ),
            bootstrapper = BootstrapperImpl(
                instrument = instrument,
                from = Calendar.getInstance().timeInMillis - (86400000 * 3),
                to = Calendar.getInstance().timeInMillis - 86400000,
                timeframe = Timeframe.CANDLE_INTERVAL_30_MIN
            ),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class FavouriteStatusChanged(val isFavourite: Boolean) : Action

        data class CandlesLoaded(val candlesList: List<Candle>) : Action

        data object CandlesStartLoading : Action

        data object CandlesLoadingError : Action
    }

    private sealed interface Msg {

        data class FavouriteStatusChanged(val isFavourite: Boolean) : Msg

        data class CandlesLoaded(val candlesList: List<Candle>) : Msg

        data object CandlesStartLoading : Msg

        data object CandlesLoadingError : Msg

        data object TimeframeOnChange : Msg

        data object PeriodOnChange : Msg

        data class TimeframeChanged(
            val timeframe: Timeframe
        ) : Msg

        data class PeriodChanged(
            val from: Long,
            val to: Long
        ) : Msg
    }

    private inner class BootstrapperImpl(
        private val instrument: Instrument,
        val from: Long,
        val to: Long,
        val timeframe: Timeframe
    ) : CoroutineBootstrapper<Action>() {

        override fun invoke() {
            scope.launch {
                observeFavouriteStateUseCase(instrument.figi).collect {
                    dispatch(Action.FavouriteStatusChanged(it))
                }
            }
            scope.launch {
                dispatch(Action.CandlesStartLoading)
                val fromCalendar = Calendar.getInstance().also { it.timeInMillis = from }
                val toCalendar = Calendar.getInstance().also { it.timeInMillis = to }
                try {
                    val candleList = getCandlesUseCase(
                        instrument.figi,
                        from = fromCalendar,
                        to = toCalendar,
                        interval = timeframe.name,
                        limit = timeframe.limit
                    )
                    dispatch(Action.CandlesLoaded(candleList))
                } catch (e: Exception) {
                    dispatch(Action.CandlesLoadingError)
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        override fun executeIntent(intent: Intent) {
            when (intent) {
                Intent.ClickBack -> {
                    publish(Label.ClickBack)
                }

                Intent.ClickChangeFavouriteStatus -> {
                    scope.launch {
                        val state = state()
                        if (state.isFavourite) {
                            changeFavouriteStateUseCase.removeFromFavourite(state.instrument.figi)
                        } else {
                            changeFavouriteStateUseCase.addToFavourite(state.instrument)
                        }
                    }
                }

                Intent.ClickChangePeriod -> {
                    dispatch(Msg.PeriodOnChange)
                }

                Intent.ClickChangeTimeframe -> {
                    dispatch(Msg.TimeframeOnChange)
                }

                is Intent.PeriodChanged -> {
                    dispatch(Msg.PeriodChanged(intent.from, intent.to))
                    val state = state()
                    val timeframeState = state.timeframeState as State.TimeframeState.SelectedTimeframe
                    dispatch(Msg.CandlesStartLoading)
                    scope.launch {
                        try {
                            val candleList = getCandlesUseCase(
                                figi = state.instrument.figi,
                                from = Calendar.getInstance().also { it.timeInMillis = intent.from},
                                to = Calendar.getInstance().also { it.timeInMillis = intent.to },
                                interval = timeframeState.timeframe.name,
                                limit = timeframeState.timeframe.limit
                            )
                            dispatch(Msg.CandlesLoaded(candleList))
                        } catch (e: Exception) {
                            dispatch(Msg.CandlesLoadingError)
                        }
                    }
                }

                is Intent.TimeframeChanged -> {
                    dispatch(Msg.TimeframeChanged(intent.timeframe))
                    val state = state()
                    val periodState = state.periodState as State.PeriodState.SelectedPeriod
                    dispatch(Msg.CandlesStartLoading)
                    scope.launch {
                        try {
                            val candleList = getCandlesUseCase(
                                figi = state.instrument.figi,
                                from = Calendar.getInstance().also { it.timeInMillis = periodState.from},
                                to = Calendar.getInstance().also { it.timeInMillis = periodState.to },
                                interval = intent.timeframe.name,
                                limit = intent.timeframe.limit
                            )
                            dispatch(Msg.CandlesLoaded(candleList))
                        } catch (e: Exception) {
                            dispatch(Msg.CandlesLoadingError)
                        }
                    }
                }
            }
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.CandlesLoaded -> {
                    dispatch(Msg.CandlesLoaded(action.candlesList))
                }

                Action.CandlesLoadingError -> {
                    dispatch(Msg.CandlesLoadingError)
                }

                Action.CandlesStartLoading -> {
                    dispatch(Msg.CandlesStartLoading)
                }

                is Action.FavouriteStatusChanged -> {
                    dispatch(Msg.FavouriteStatusChanged(action.isFavourite))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {

        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.CandlesLoaded -> {
                copy(candlesState = State.CandlesState.Loaded(msg.candlesList))
            }

            Msg.CandlesLoadingError -> {
                copy(candlesState = State.CandlesState.Error)
            }

            Msg.CandlesStartLoading -> {
                copy(candlesState = State.CandlesState.Loading)
            }

            is Msg.FavouriteStatusChanged -> {
                copy(isFavourite = msg.isFavourite)
            }

            is Msg.PeriodChanged -> {
                copy(periodState = State.PeriodState.SelectedPeriod(msg.from, msg.to))
            }

            Msg.PeriodOnChange -> {
                copy(periodState = State.PeriodState.OnChange)
            }

            is Msg.TimeframeChanged -> {
                copy(timeframeState = State.TimeframeState.SelectedTimeframe(msg.timeframe))
            }

            Msg.TimeframeOnChange -> {
                copy(timeframeState = State.TimeframeState.OnChange)
            }
        }
    }
}
