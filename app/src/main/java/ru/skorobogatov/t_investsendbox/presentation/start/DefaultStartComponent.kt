package ru.skorobogatov.t_investsendbox.presentation.start

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
import ru.skorobogatov.t_investsendbox.presentation.extantions.componentScope

class DefaultStartComponent @AssistedInject constructor(
    private val startStoreFactory: StartStoreFactory,
    @Assisted("onClickedGoToFavourite") private val onClickedGoToFavourite: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : StartComponent, ComponentContext by componentContext{

    private val store = instanceKeeper.getStore { startStoreFactory.create() }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when(it){
                    StartStore.Label.ClickGoToFavourite -> {
                        onClickedGoToFavourite()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<StartStore.State> = store.stateFlow

    override fun onClickSave(token: String) {
        store.accept(StartStore.Intent.ClickSave(token))
    }

    override fun onClickCheck() {
        store.accept(StartStore.Intent.ClickCheck)
    }

    override fun onClickGoToFavourite() {
        store.accept(StartStore.Intent.ClickGoToFavourite)
    }

    @AssistedFactory
    interface Factory{

        fun create(
            @Assisted("onClickedGoToFavourite") onClickedGoToFavourite: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultStartComponent
    }
}