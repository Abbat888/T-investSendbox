package ru.skorobogatov.t_investsendbox.presentation.start

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.skorobogatov.t_investsendbox.domain.usecase.CheckTokenUseCase
import ru.skorobogatov.t_investsendbox.domain.usecase.GetInfoUseCase
import ru.skorobogatov.t_investsendbox.domain.usecase.RegistrationTokenUseCase
import ru.skorobogatov.t_investsendbox.presentation.start.StartStore.Intent
import ru.skorobogatov.t_investsendbox.presentation.start.StartStore.Label
import ru.skorobogatov.t_investsendbox.presentation.start.StartStore.State
import ru.skorobogatov.t_investsendbox.presentation.start.StartStoreFactory.Msg.*
import javax.inject.Inject

interface StartStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data class ClickSave(
            val token: String
        ) : Intent

        data object ClickCheck : Intent

        data object ClickGoToFavourite: Intent
    }

    data class State(
        val tokenState: TokenState
    ) {

        sealed interface TokenState {

            data object TokenOnChecking : TokenState

            data class TokenIsNotEmpty(
                val isNotEmpty: Boolean
            ) : TokenState

            data class TokenIsValid(
                val isValid: Boolean
            ) : TokenState
        }
    }

    sealed interface Label {

        data object ClickGoToFavourite: Label
    }
}

class StartStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val checkTokenUseCase: CheckTokenUseCase,
    private val getInfoUseCase: GetInfoUseCase,
    private val registrationTokenUseCase: RegistrationTokenUseCase
) {

    fun create(): StartStore =
        object : StartStore, Store<Intent, State, Label> by storeFactory.create(
            name = "StartStore",
            initialState = State(tokenState = State.TokenState.TokenOnChecking),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class InfoIsLoaded(
            val tokenIsValid: Boolean
        ) : Action

        data object CheckingToken : Action

        data class TokenChecked(
            val tokenIsNotEmpty: Boolean
        ) : Action
    }

    private sealed interface Msg {

        data class InfoIsLoaded(
            val tokenIsValid: Boolean
        ) : Msg

        data object CheckingToken : Msg

        data class TokenChecked(
            val tokenIsNotEmpty: Boolean
        ) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.CheckingToken)
                val tokenIsNotEmpty = checkTokenUseCase()
                dispatch(Action.TokenChecked(tokenIsNotEmpty))
                if (tokenIsNotEmpty) {
                    val tokenIsValid = getInfoUseCase()==200
                    dispatch(Action.InfoIsLoaded(tokenIsValid))
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.ClickSave -> {
                    scope.launch {
                        registrationTokenUseCase(intent.token)
                    }
                }

                Intent.ClickCheck -> {
                    scope.launch {
                        dispatch(Msg.CheckingToken)
                        val tokenIsNotEmpty = checkTokenUseCase()
                        dispatch(TokenChecked(tokenIsNotEmpty))
                        if (tokenIsNotEmpty) {
                            val tokenIsValid = getInfoUseCase()==200
                            dispatch(InfoIsLoaded(tokenIsValid))
                        }
                    }
                }

                Intent.ClickGoToFavourite -> {
                    publish(Label.ClickGoToFavourite)
                }
            }
        }

        override fun executeAction(action: Action) {
            when (action) {
                Action.CheckingToken -> {
                    dispatch(Msg.CheckingToken)
                }

                is Action.InfoIsLoaded -> {
                    dispatch(Msg.InfoIsLoaded(action.tokenIsValid))
                }

                is Action.TokenChecked -> {
                    dispatch(Msg.TokenChecked(action.tokenIsNotEmpty))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                Msg.CheckingToken -> {
                    copy(State.TokenState.TokenOnChecking)
                }

                is Msg.InfoIsLoaded -> {
                    copy(State.TokenState.TokenIsValid(msg.tokenIsValid))
                }

                is Msg.TokenChecked -> {
                    copy(State.TokenState.TokenIsNotEmpty(msg.tokenIsNotEmpty))
                }
            }
    }
}
