package ru.skorobogatov.t_investsendbox.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.skorobogatov.t_investsendbox.presentation.ui.theme.TinvestSendboxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TinvestSendboxTheme {

            }
        }
    }
}