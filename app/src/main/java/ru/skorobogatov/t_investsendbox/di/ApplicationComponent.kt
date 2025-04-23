package ru.skorobogatov.t_investsendbox.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.skorobogatov.t_investsendbox.presentation.MainActivity

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        PresentationModule::class,
        TokenModule::class
    ]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance context: Context
        ): ApplicationComponent
    }
}