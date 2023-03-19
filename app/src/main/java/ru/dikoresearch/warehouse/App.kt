package ru.dikoresearch.warehouse

import android.app.Application
import androidx.work.Configuration
import ru.dikoresearch.warehouse.di.AppComponent
import ru.dikoresearch.warehouse.di.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject

class App: Application(), Configuration.Provider {
    lateinit var appComponent: AppComponent

    @Inject
    lateinit var workerConfiguration: Configuration

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        appComponent = DaggerAppComponent.builder()
            .context(this)
            .build()

        appComponent.inject(this)

    }

    /*
    We need this override method to provide DI in WorkManager.
     */
    override fun getWorkManagerConfiguration(): Configuration {
        return workerConfiguration
    }
}

