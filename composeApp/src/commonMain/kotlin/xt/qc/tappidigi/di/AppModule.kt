package xt.qc.tappidigi.di

import org.koin.core.module.Module

expect class AppModule {
    val appModule: Module

    fun initKoin()
}