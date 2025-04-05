package ru.skorobogatov.t_investsendbox

import android.app.Application
import ru.skorobogatov.t_investsendbox.di.ApplicationComponent
import ru.skorobogatov.t_investsendbox.di.DaggerApplicationComponent

class TInvestSendBoxApp : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }
}