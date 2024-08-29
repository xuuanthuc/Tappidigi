package xt.qc.tappidigi.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import xt.qc.tappidigi.AppViewModel
import xt.qc.tappidigi.screens.profile.ProfileViewModel
import xt.qc.tappidigi.utils.Platform

actual class AppModule {
    actual val appModule: Module = module {
        singleOf(::Platform)
        singleOf(::AppViewModel)
        singleOf(::ProfileViewModel)
    }
    actual fun initKoin() {
        startKoin {
            modules(appModule)
        }
    }
}