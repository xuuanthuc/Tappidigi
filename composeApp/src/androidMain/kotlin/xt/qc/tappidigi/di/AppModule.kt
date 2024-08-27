package xt.qc.tappidigi.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import xt.qc.tappidigi.GreetingViewModel
import xt.qc.tappidigi.Platform

actual class AppModule(private val context: Context) {
    actual val appModule: Module = module {
        singleOf(::GreetingViewModel)
        singleOf(::Platform)
    }
    actual fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(context)
            modules(appModule)
        }
    }
}