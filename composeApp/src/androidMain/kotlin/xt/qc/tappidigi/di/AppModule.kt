package xt.qc.tappidigi.di

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import xt.qc.tappidigi.AppViewModel
import xt.qc.tappidigi.MainApplication
import xt.qc.tappidigi.api.ApiProvider
import xt.qc.tappidigi.screens.authentication.SignInWithGoogleManager
import xt.qc.tappidigi.screens.profile.ProfileViewModel
import xt.qc.tappidigi.utils.Platform


actual class AppModule(private val context: Context) {
    actual val appModule: Module = module {
        singleOf(::AppViewModel)
        singleOf(::ProfileViewModel)
        singleOf(::ApiProvider)
        single { CredentialManager.create((context.applicationContext as MainApplication).getCurrentActivity()!!) }
        single { SignInWithGoogleManager(context, get())  }
        single { Platform(context, (context.applicationContext as MainApplication).getCurrentActivity()!!) }
    }

    actual fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(context)
            modules(appModule)
        }
    }
}