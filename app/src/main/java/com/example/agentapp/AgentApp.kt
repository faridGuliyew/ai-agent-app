package com.example.agentapp

import android.app.Application
import com.example.agentapp.di.appModule
import com.example.agentapp.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AgentApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AgentApp)
            modules(appModule, networkModule)
        }
    }
}