package xt.qc.tappidigi.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import xt.qc.tappidigi.GreetingViewModel
import xt.qc.tappidigi.Platform

actual class AppModule {
    actual val appModule: Module = module {
        singleOf(::GreetingViewModel)
        singleOf(::Platform)
    }
    actual fun initKoin() {
        startKoin {
            modules(appModule)
        }
    }
}