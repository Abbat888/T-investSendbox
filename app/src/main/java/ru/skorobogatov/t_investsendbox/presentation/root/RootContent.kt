package ru.skorobogatov.t_investsendbox.presentation.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.skorobogatov.t_investsendbox.presentation.details.DetailsComponent
import ru.skorobogatov.t_investsendbox.presentation.details.DetailsContent
import ru.skorobogatov.t_investsendbox.presentation.favourite.FavouriteContent
import ru.skorobogatov.t_investsendbox.presentation.search.SearchContent
import ru.skorobogatov.t_investsendbox.presentation.start.StartContent
import ru.skorobogatov.t_investsendbox.presentation.ui.theme.TinvestSendboxTheme

@Composable
fun RootContent(
    component: RootComponent
){
    TinvestSendboxTheme {
        Children(
            stack = component.stack
        ) {
            when(val instance = it.instance){
                is RootComponent.Child.Details -> {
                    DetailsContent(instance.component)
                }
                is RootComponent.Child.Favourite -> {
                    FavouriteContent(instance.component)
                }
                is RootComponent.Child.Search -> {
                    SearchContent(instance.component)
                }

                is RootComponent.Child.Start -> {
                    StartContent(instance.component)
                }
            }
        }
    }
}