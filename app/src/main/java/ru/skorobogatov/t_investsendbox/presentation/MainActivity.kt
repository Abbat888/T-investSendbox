package ru.skorobogatov.t_investsendbox.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import ru.skorobogatov.t_investsendbox.TInvestSendBoxApp
import ru.skorobogatov.t_investsendbox.presentation.root.DefaultRootComponent
import ru.skorobogatov.t_investsendbox.presentation.root.RootContent
import ru.skorobogatov.t_investsendbox.presentation.ui.theme.TinvestSendboxTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var rootComponentFactory: DefaultRootComponent.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TInvestSendBoxApp).applicationComponent.inject(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RootContent(component = rootComponentFactory.create(defaultComponentContext()))
        }
    }
}