package ru.skorobogatov.t_investsendbox.presentation.details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument
import ru.skorobogatov.t_investsendbox.domain.entity.Timeframe
import ru.skorobogatov.t_investsendbox.presentation.extantions.componentScope

class DefaultDetailsComponent @AssistedInject constructor(
    private val detailsStoreFactory: DetailsStoreFactory,
    @Assisted("instrument") private val instrument: Instrument,
    @Assisted("onBackClicked") private val onBackClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : DetailsComponent, ComponentContext by componentContext{

    private val store = instanceKeeper.getStore { detailsStoreFactory.create(instrument) }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when(it){
                    DetailsStore.Label.ClickBack -> {
                        onBackClicked()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<DetailsStore.State> = store.stateFlow

    override fun onClickBack() {
        store.accept(DetailsStore.Intent.ClickBack)
    }

    override fun onClickChangeFavouriteStatus() {
        store.accept(DetailsStore.Intent.ClickChangeFavouriteStatus)
    }

    override fun onClickChangeTimeframe() {
        store.accept(DetailsStore.Intent.ClickChangeTimeframe)
    }

    override fun onClickChangePeriod() {
        store.accept(DetailsStore.Intent.ClickChangePeriod)
    }

    override fun onTimeFrameChanged(timeframe: Timeframe) {
        store.accept(DetailsStore.Intent.TimeframeChanged(timeframe))
    }

    override fun onPeriodChanged(from: Long, to: Long) {
        store.accept(DetailsStore.Intent.PeriodChanged(from = from, to = to))
    }

    @AssistedFactory
    interface Factory{

        fun create(
            @Assisted("instrument") instrument: Instrument,
            @Assisted("onBackClicked") onBackClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultDetailsComponent
    }
}