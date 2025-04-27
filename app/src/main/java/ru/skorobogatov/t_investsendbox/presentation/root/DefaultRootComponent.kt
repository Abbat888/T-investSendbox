package ru.skorobogatov.t_investsendbox.presentation.root

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.parcelize.Parcelize
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument
import ru.skorobogatov.t_investsendbox.presentation.details.DefaultDetailsComponent
import ru.skorobogatov.t_investsendbox.presentation.favourite.DefaultFavouriteComponent
import ru.skorobogatov.t_investsendbox.presentation.search.DefaultSearchComponent
import ru.skorobogatov.t_investsendbox.presentation.start.DefaultStartComponent

class DefaultRootComponent @AssistedInject constructor(
    private val startFactory: DefaultStartComponent.Factory,
    private val favouriteFactory: DefaultFavouriteComponent.Factory,
    private val detailsFactory: DefaultDetailsComponent.Factory,
    private val searchFactory: DefaultSearchComponent.Factory,
    @Assisted("componentContext") componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = null,
        initialConfiguration = Config.Start,
        handleBackButton = true,
        childFactory = ::child
    )

    @OptIn(DelicateDecomposeApi::class)
    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child {
        return when (config) {
            is Config.Details -> {
                val component = detailsFactory.create(
                    instrument = config.instrument,
                    onBackClicked = {
                        navigation.pop()
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Details(component)
            }

            Config.Favourite -> {
                val component = favouriteFactory.create(
                    onInstrumentItemClicked = {
                        navigation.push(Config.Details(it))
                    },
                    onSearchClicked = {
                        navigation.push(Config.Search)
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Favourite(component)
            }

            Config.Search -> {
                val component = searchFactory.create(
                    onBackClicked = {
                        navigation.pop()
                    },
                    onInstrumentItemClicked = {
                        navigation.push(Config.Details(it))
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Search(component)
            }

            Config.Start -> {
                val component = startFactory.create(
                    onClickedGoToFavourite = {
                        navigation.push(Config.Favourite)
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Start(component)
            }
        }
    }

    sealed interface Config : Parcelable {

        @Parcelize
        data object Start: Config

        @Parcelize
        data object Favourite : Config

        @Parcelize
        data class Details(
            val instrument: Instrument
        ) : Config

        @Parcelize
        data object Search : Config
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultRootComponent
    }
}