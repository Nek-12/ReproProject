package com.nek12.ktordeadlockrepro

import android.app.Application
import com.nek12.ktordeadlockrepro.network.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(networkModule)
        }
    }
}
