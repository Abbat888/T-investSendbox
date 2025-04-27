package ru.skorobogatov.t_investsendbox.presentation.start

import kotlinx.coroutines.flow.StateFlow

interface StartComponent {

    val model: StateFlow<StartStore.State>

    fun onClickSave(token: String)

    fun onClickCheck()

    fun onClickGoToFavourite()
}